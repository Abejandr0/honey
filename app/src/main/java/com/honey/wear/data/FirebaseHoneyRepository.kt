package com.honey.wear.data

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await
import java.security.SecureRandom
import java.time.LocalDate
import java.util.Date

/** Firebase-backed identity and couple data. It never supplies sample users or coordinates. */
class FirebaseHoneyRepository(context: Context) {
    private val app: FirebaseApp? = runCatching {
        FirebaseApp.getApps(context).firstOrNull() ?: FirebaseApp.initializeApp(context)
    }.getOrNull()

    val isConfigured: Boolean = app != null

    private val auth: FirebaseAuth? = app?.let(FirebaseAuth::getInstance)
    private val firestore: FirebaseFirestore? = app?.let(FirebaseFirestore::getInstance)

    val currentUser: FirebaseUser? get() = auth?.currentUser

    fun addAuthStateListener(onChanged: (FirebaseUser?) -> Unit): AuthStateListener? {
        val firebaseAuth = auth ?: return null
        val listener = AuthStateListener { onChanged(it.currentUser) }
        firebaseAuth.addAuthStateListener(listener)
        return listener
    }

    fun removeAuthStateListener(listener: AuthStateListener?) {
        if (listener != null) auth?.removeAuthStateListener(listener)
    }

    suspend fun signInAnonymously() {
        if (currentUser == null) requireAuth().signInAnonymously().await()
    }

    suspend fun createProfile(name: String) {
        val user = requireAuth().currentUser ?: error("No active Firebase identity")
        val username = name.trim()
        require(username.isNotEmpty() && username.length <= 40)
        val profile = mapOf(
            "uid" to user.uid,
            "username" to username,
            "pairingCode" to null,
            "partnerId" to null,
            "coupleId" to null,
            "anniversaryDate" to null,
            "mood" to null,
            "locationSharingEnabled" to false,
            "dndActive" to false,
            "createdAt" to FieldValue.serverTimestamp()
        )
        requireFirestore().collection(USERS).document(user.uid).set(profile).await()
    }

    fun observeProfile(
        uid: String,
        onProfile: (UserProfile?) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration = requireFirestore()
        .collection(USERS)
        .document(uid)
        .addSnapshotListener { snapshot, error ->
            if (error != null) onError(error)
            else onProfile(snapshot?.toUserProfile())
        }

    fun observePartner(
        partnerId: String,
        onPartner: (PartnerState?) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration = requireFirestore()
        .collection(USERS)
        .document(partnerId)
        .addSnapshotListener { snapshot, error ->
            if (error != null) onError(error)
            else onPartner(snapshot?.toPartnerState())
        }

    fun observeLocation(
        coupleId: String,
        partnerId: String,
        onLocation: (SharedLocation?) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration = requireFirestore()
        .collection(COUPLES)
        .document(coupleId)
        .collection(LOCATIONS)
        .document(partnerId)
        .addSnapshotListener { snapshot, error ->
            if (error != null) onError(error)
            else onLocation(snapshot?.toSharedLocation())
        }

    fun observeMessages(
        coupleId: String,
        onMessages: (List<HoneyMessage>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration = requireFirestore()
        .collection(COUPLES)
        .document(coupleId)
        .collection(MESSAGES)
        .orderBy("createdAt", Query.Direction.ASCENDING)
        .limitToLast(40)
        .addSnapshotListener { snapshot, error ->
            if (error != null) onError(error)
            else onMessages(snapshot?.documents.orEmpty().mapNotNull { it.toHoneyMessage() })
        }

    fun observePendingPair(
        code: String,
        onPaired: () -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration = requireFirestore()
        .collection(COUPLES)
        .document(code)
        .addSnapshotListener { snapshot, error ->
            if (error != null) onError(error)
            else if (snapshot?.exists() == true) onPaired()
        }

    suspend fun createPairingCode(): String {
        val db = requireFirestore()
        val uid = requireUserId()
        val userRef = db.collection(USERS).document(uid)
        repeat(5) {
            val code = newPairingCode()
            val codeRef = db.collection(PAIRING_CODES).document(code)
            val created = db.runTransaction { transaction ->
                val user = transaction.get(userRef)
                val existingCode = transaction.get(codeRef)
                val previousCode = user.getString("pairingCode")
                val previousPair = previousCode?.let { old ->
                    transaction.get(db.collection(COUPLES).document(old))
                }
                check(user.exists() && user.getString("coupleId") == null) { "Profile is unavailable or already paired" }
                check(previousPair?.exists() != true) { "Pairing is already in progress" }
                if (existingCode.exists()) false
                else {
                    previousCode?.let { oldCode ->
                        transaction.delete(db.collection(PAIRING_CODES).document(oldCode))
                    }
                    transaction.set(codeRef, mapOf(
                        "ownerUid" to uid,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "expiresAt" to Timestamp(Date(System.currentTimeMillis() + CODE_LIFETIME_MS))
                    ))
                    transaction.update(userRef, "pairingCode", code)
                    true
                }
            }.await()
            if (created) return code
        }
        error("Could not create a unique pairing code")
    }

    suspend fun pairWithCode(code: String, anniversaryDate: LocalDate?) {
        val normalized = code.trim().uppercase().filter(Char::isLetterOrDigit)
        require(normalized.matches(Regex("HNY[A-HJ-NP-Z2-9]{10}"))) { "Invalid pairing code" }
        val formattedCode = "HNY-${normalized.drop(3)}"
        val db = requireFirestore()
        val uid = requireUserId()
        val codeRef = db.collection(PAIRING_CODES).document(formattedCode)
        val coupleRef = db.collection(COUPLES).document(formattedCode)
        val userRef = db.collection(USERS).document(uid)
        db.runTransaction { transaction ->
            val invitation = transaction.get(codeRef)
            val couple = transaction.get(coupleRef)
            val user = transaction.get(userRef)
            val ownerUid = invitation.getString("ownerUid")
            check(invitation.exists() && ownerUid != null && ownerUid != uid) { "Code is unavailable" }
            check(invitation.getTimestamp("expiresAt")?.toDate()?.after(Date()) == true) { "Code has expired" }
            check(!couple.exists() && user.exists() && user.getString("coupleId") == null) { "Already paired" }
            transaction.set(coupleRef, mapOf(
                "memberIds" to listOf(ownerUid, uid),
                "createdAt" to FieldValue.serverTimestamp(),
                "anniversaryDate" to anniversaryDate?.toString()
            ))
            transaction.update(userRef, mapOf(
                "coupleId" to formattedCode,
                "partnerId" to ownerUid,
                "pairingCode" to null,
                "anniversaryDate" to anniversaryDate?.toString()
            ))
            user.getString("pairingCode")?.let { ownCode ->
                transaction.delete(db.collection(PAIRING_CODES).document(ownCode))
            }
        }.await()
    }

    suspend fun finishPendingPair(code: String) {
        val db = requireFirestore()
        val uid = requireUserId()
        val couple = db.collection(COUPLES).document(code).get().await()
        val members = couple.get("memberIds") as? List<*> ?: return
        if (uid !in members || members.size != 2) return
        val partnerId = members.firstOrNull { it != uid } as? String ?: return
        db.collection(USERS).document(uid).update(mapOf(
            "coupleId" to code,
            "partnerId" to partnerId,
            "pairingCode" to null,
            "anniversaryDate" to couple.getString("anniversaryDate")
        )).await()
    }

    suspend fun updateMood(mood: MoodType) {
        val uid = requireUserId()
        requireFirestore().collection(USERS).document(uid).update(
            mapOf("mood" to mood.name, "moodUpdatedAt" to FieldValue.serverTimestamp())
        ).await()
    }

    suspend fun setDndActive(enabled: Boolean) {
        val uid = requireUserId()
        requireFirestore().collection(USERS).document(uid).update("dndActive", enabled).await()
    }

    suspend fun setLocationSharing(enabled: Boolean) {
        val uid = requireUserId()
        val profile = requireFirestore().collection(USERS).document(uid)
        profile.update("locationSharingEnabled", enabled).await()

        val coupleId = profile.get().await().getString("coupleId") ?: return
        if (!enabled) {
            val ownLocation = requireFirestore()
                .collection(COUPLES)
                .document(coupleId)
                .collection(LOCATIONS)
                .document(uid)
            ownLocation.delete().await()
        }
    }

    suspend fun publishLocation(latitude: Double, longitude: Double, accuracyMeters: Float?) {
        val uid = requireUserId()
        val profile = requireFirestore().collection(USERS).document(uid).get().await()
        if (profile.getBoolean("locationSharingEnabled") != true) return
        val coupleId = profile.getString("coupleId") ?: return

        val location = mutableMapOf<String, Any>(
            "uid" to uid,
            "latitude" to latitude,
            "longitude" to longitude,
            "sharing" to true,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (accuracyMeters != null) location["accuracyMeters"] = accuracyMeters
        requireFirestore()
            .collection(COUPLES)
            .document(coupleId)
            .collection(LOCATIONS)
            .document(uid)
            .set(location)
            .await()
    }

    suspend fun sendMessage(text: String) = sendEvent(
        kind = "message",
        values = mapOf("text" to text.trim())
    )

    suspend fun sendTouch(type: TouchType) = sendEvent(
        kind = "touch",
        values = mapOf("touchType" to type.name)
    )

    private suspend fun sendEvent(kind: String, values: Map<String, Any>) {
        val uid = requireUserId()
        val profile = requireFirestore().collection(USERS).document(uid).get().await()
        val coupleId = profile.getString("coupleId") ?: error("This account is not linked to a partner")
        val recipientId = profile.getString("partnerId") ?: error("This account is not linked to a partner")
        val event = mutableMapOf<String, Any>(
            "senderUid" to uid,
            "recipientUid" to recipientId,
            "kind" to kind,
            "createdAt" to FieldValue.serverTimestamp()
        )
        event.putAll(values)
        requireFirestore()
            .collection(COUPLES)
            .document(coupleId)
            .collection(MESSAGES)
            .add(event)
            .await()
    }

    private fun requireAuth(): FirebaseAuth = auth ?: error("Firebase Authentication is not configured")
    private fun requireFirestore(): FirebaseFirestore = firestore ?: error("Cloud Firestore is not configured")
    private fun requireUserId(): String = currentUser?.uid ?: error("Firebase identity is unavailable")

    private fun newPairingCode(): String {
        val random = SecureRandom()
        return "HNY-" + (1..10).map { CODE_ALPHABET[random.nextInt(CODE_ALPHABET.length)] }.joinToString("")
    }

    private fun DocumentSnapshot.toUserProfile(): UserProfile? {
        if (!exists()) return null
        val uid = getString("uid") ?: id
        val username = getString("username") ?: return null
        return UserProfile(
            uid = uid,
            username = username,
            pairingCode = getString("pairingCode"),
            partnerId = getString("partnerId"),
            coupleId = getString("coupleId"),
            anniversaryDate = getString("anniversaryDate")?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
            mood = getString("mood")?.let { value -> enumValueOrNull<MoodType>(value) },
            isSharingLocation = getBoolean("locationSharingEnabled") ?: false
        )
    }

    private fun DocumentSnapshot.toPartnerState(): PartnerState? {
        if (!exists()) return null
        val name = getString("username") ?: return null
        return PartnerState(
            uid = getString("uid") ?: id,
            name = name,
            mood = getString("mood")?.let { value -> enumValueOrNull<MoodType>(value) },
            isSharingLocation = getBoolean("locationSharingEnabled") ?: false,
            isDndActive = getBoolean("dndActive") ?: false
        )
    }

    private fun DocumentSnapshot.toSharedLocation(): SharedLocation? {
        if (!exists() || getBoolean("sharing") != true) return null
        val latitude = getDouble("latitude") ?: return null
        val longitude = getDouble("longitude") ?: return null
        return SharedLocation(
            latitude = latitude,
            longitude = longitude,
            accuracyMeters = getDouble("accuracyMeters")?.toFloat(),
            updatedAtMillis = getTimestamp("updatedAt")?.toDate()?.time ?: 0L
        )
    }

    private fun DocumentSnapshot.toHoneyMessage(): HoneyMessage? {
        val senderUid = getString("senderUid") ?: return null
        val kind = getString("kind") ?: return null
        return HoneyMessage(
            id = id,
            senderUid = senderUid,
            kind = kind,
            text = getString("text"),
            touchType = getString("touchType")?.let { value -> enumValueOrNull<TouchType>(value) },
            createdAtMillis = getTimestamp("createdAt")?.toDate()?.time ?: 0L
        )
    }

    private inline fun <reified T : Enum<T>> enumValueOrNull(value: String): T? =
        runCatching { enumValueOf<T>(value) }.getOrNull()

    private companion object {
        const val PAIRING_CODES = "pairingCodes"
        const val CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        const val CODE_LIFETIME_MS = 24 * 60 * 60 * 1000L
        const val USERS = "users"
        const val COUPLES = "couples"
        const val LOCATIONS = "locations"
        const val MESSAGES = "messages"
    }
}
