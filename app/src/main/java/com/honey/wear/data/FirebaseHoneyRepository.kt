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
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

/** Firebase-backed account and couple data. It never supplies sample users or coordinates. */
class FirebaseHoneyRepository(context: Context) {
    private val app: FirebaseApp? = runCatching {
        FirebaseApp.getApps(context).firstOrNull() ?: FirebaseApp.initializeApp(context)
    }.getOrNull()

    val isConfigured: Boolean = app != null

    private val auth: FirebaseAuth? = app?.let(FirebaseAuth::getInstance)
    private val firestore: FirebaseFirestore? = app?.let(FirebaseFirestore::getInstance)
    private val functions: FirebaseFunctions? = app?.let {
        FirebaseFunctions.getInstance(it, FUNCTIONS_REGION)
    }

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

    suspend fun register(name: String, email: String, password: String) {
        val firebaseAuth = requireAuth()
        val user = firebaseAuth.createUserWithEmailAndPassword(email.trim(), password).await().user
            ?: error("Firebase did not return a user after registration")
        val profile = mapOf(
            "uid" to user.uid,
            "username" to name.trim(),
            "pairingCode" to null,
            "partnerId" to null,
            "coupleId" to null,
            "anniversaryDate" to null,
            "mood" to null,
            "locationSharingEnabled" to false,
            "dndActive" to false,
            "createdAt" to FieldValue.serverTimestamp()
        )
        try {
            requireFirestore().collection(USERS).document(user.uid).set(profile).await()
        } catch (error: Exception) {
            runCatching { user.delete().await() }
            firebaseAuth.signOut()
            throw error
        }
    }

    suspend fun signIn(email: String, password: String) {
        requireAuth().signInWithEmailAndPassword(email.trim(), password).await()
    }

    fun signOut() {
        auth?.signOut()
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

    suspend fun createPairingCode(): String {
        val response = requireFunctions()
            .getHttpsCallable("createPairingCode")
            .call()
            .await()
            .data as? Map<*, *> ?: error("Pairing service returned an invalid response")
        return response["code"] as? String ?: error("Pairing service did not return a code")
    }

    suspend fun pairWithCode(code: String, anniversaryDate: LocalDate?) {
        val data = buildMap<String, Any> {
            put("code", code.trim().uppercase())
            anniversaryDate?.let { put("anniversaryDate", it.toString()) }
        }
        requireFunctions()
            .getHttpsCallable("pairWithCode")
            .call(data)
            .await()
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
    private fun requireFunctions(): FirebaseFunctions = functions ?: error("Firebase Functions is not configured")
    private fun requireUserId(): String = currentUser?.uid ?: error("Sign in before using Honey")

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
        const val FUNCTIONS_REGION = "us-central1"
        const val USERS = "users"
        const val COUPLES = "couples"
        const val LOCATIONS = "locations"
        const val MESSAGES = "messages"
    }
}
