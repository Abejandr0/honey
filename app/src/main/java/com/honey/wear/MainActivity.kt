package com.honey.wear

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import android.os.LocaleList
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.firestore.ListenerRegistration
import com.honey.wear.data.FirebaseHoneyRepository
import com.honey.wear.data.HoneyMessage
import com.honey.wear.data.HoneyRepository
import com.honey.wear.data.MoodType
import com.honey.wear.data.PartnerState
import com.honey.wear.data.SharedLocation
import com.honey.wear.data.TouchType
import com.honey.wear.data.UserProfile
import com.honey.wear.location.LocationShareService
import com.honey.wear.ui.components.TouchReactionOverlay
import com.honey.wear.ui.screens.*
import com.honey.wear.ui.theme.HoneyWearTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class ScreenRoute {
    HOME,
    TOUCH,
    MESSAGES,
    LOCATION,
    MOOD,
    SETTINGS
}

class MainActivity : ComponentActivity() {
    private lateinit var localRepository: HoneyRepository
    private lateinit var firebaseRepository: FirebaseHoneyRepository

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            enableLocationSharing()
        } else {
            Toast.makeText(this, R.string.location_permission_required, Toast.LENGTH_LONG).show()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val language = newBase
            .getSharedPreferences("honey_preferences", Context.MODE_PRIVATE)
            .getString("language", "es") ?: "es"
        val configuration = Configuration(newBase.resources.configuration).apply {
            setLocales(LocaleList.forLanguageTags(language))
        }
        super.attachBaseContext(newBase.createConfigurationContext(configuration))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        localRepository = HoneyRepository(applicationContext)
        firebaseRepository = FirebaseHoneyRepository(applicationContext)

        setContent {
            val settings by localRepository.settings.collectAsState()
            val context = LocalContext.current
            val scope = rememberCoroutineScope()

            var firebaseUser by remember { mutableStateOf(firebaseRepository.currentUser) }
            var userProfile by remember { mutableStateOf<UserProfile?>(null) }
            var partnerProfile by remember { mutableStateOf<PartnerState?>(null) }
            var partnerLocation by remember { mutableStateOf<SharedLocation?>(null) }
            var myLocation by remember { mutableStateOf<SharedLocation?>(null) }
            var messages by remember { mutableStateOf<List<HoneyMessage>>(emptyList()) }
            var profileLoading by remember { mutableStateOf(false) }
            var partnerLoading by remember { mutableStateOf(false) }
            var authLoading by remember { mutableStateOf(false) }
            var pairingLoading by remember { mutableStateOf(false) }
            var codeLoading by remember { mutableStateOf(false) }
            var authError by remember { mutableStateOf<String?>(null) }
            var appError by remember { mutableStateOf<String?>(null) }
            var pairingCodeOverride by remember { mutableStateOf<String?>(null) }
            var receivedTouch by remember { mutableStateOf<TouchType?>(null) }
            var currentRoute by remember { mutableStateOf(ScreenRoute.HOME) }

            val currentUid = firebaseUser?.uid
            val sessionStartedAt = remember { System.currentTimeMillis() }
            val processedMessageIds = remember { mutableSetOf<String>() }

            DisposableEffect(Unit) {
                val listener: AuthStateListener? = firebaseRepository.addAuthStateListener { nextUser ->
                    firebaseUser = nextUser
                    authError = null
                    appError = null
                    if (nextUser == null) {
                        userProfile = null
                        partnerProfile = null
                        partnerLocation = null
                        myLocation = null
                        messages = emptyList()
                        currentRoute = ScreenRoute.HOME
                    }
                }
                onDispose { firebaseRepository.removeAuthStateListener(listener) }
            }

            DisposableEffect(currentUid) {
                val uid = currentUid
                var registration: ListenerRegistration? = null
                if (uid == null) {
                    userProfile = null
                    profileLoading = false
                } else {
                    profileLoading = true
                    registration = firebaseRepository.observeProfile(
                        uid = uid,
                        onProfile = { profile ->
                            userProfile = profile
                            pairingCodeOverride = profile?.pairingCode
                            profileLoading = false
                        },
                        onError = {
                            profileLoading = false
                            appError = getString(R.string.firebase_profile_error)
                        }
                    )
                }
                onDispose { registration?.remove() }
            }

            DisposableEffect(userProfile?.coupleId, userProfile?.partnerId, currentUid) {
                val coupleId = userProfile?.coupleId
                val partnerId = userProfile?.partnerId
                val uid = currentUid
                val registrations = mutableListOf<ListenerRegistration>()
                if (coupleId.isNullOrBlank() || partnerId.isNullOrBlank() || uid.isNullOrBlank()) {
                    partnerProfile = null
                    partnerLocation = null
                    myLocation = null
                    messages = emptyList()
                    partnerLoading = false
                } else {
                    partnerLoading = true
                    registrations += firebaseRepository.observePartner(
                        partnerId,
                        onPartner = {
                            partnerProfile = it
                            partnerLoading = false
                        },
                        onError = {
                            partnerLoading = false
                            appError = getString(R.string.firebase_profile_error)
                        }
                    )
                    registrations += firebaseRepository.observeLocation(
                        coupleId,
                        partnerId,
                        onLocation = { partnerLocation = it },
                        onError = { appError = getString(R.string.firebase_generic_error) }
                    )
                    registrations += firebaseRepository.observeLocation(
                        coupleId,
                        uid,
                        onLocation = { myLocation = it },
                        onError = { appError = getString(R.string.firebase_generic_error) }
                    )
                    registrations += firebaseRepository.observeMessages(
                        coupleId,
                        onMessages = { messages = it },
                        onError = { appError = getString(R.string.firebase_generic_error) }
                    )
                }
                onDispose { registrations.forEach(ListenerRegistration::remove) }
            }

            LaunchedEffect(messages, currentUid, settings.isDndActive) {
                val uid = currentUid ?: return@LaunchedEffect
                messages.forEach { message ->
                    if (message.id in processedMessageIds) return@forEach
                    processedMessageIds += message.id
                    if (message.senderUid == uid || message.createdAtMillis < sessionStartedAt) return@forEach
                    if (settings.isDndActive) return@forEach

                    when (message.kind) {
                        "touch" -> message.touchType?.let { touch ->
                            localRepository.performHapticTouch(touch)
                            receivedTouch = touch
                        }
                        "message" -> {
                            localRepository.hapticConfirmation()
                            Toast.makeText(
                                context,
                                getString(R.string.message_received_from, partnerProfile?.name.orEmpty()),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }

            LaunchedEffect(userProfile?.isSharingLocation, currentUid) {
                when (userProfile?.isSharingLocation) {
                    true -> if (hasLocationPermission()) startLocationShareService()
                    false -> stopLocationShareService()
                    null -> Unit
                }
            }

            HoneyWearTheme(
                selectedTheme = settings.themePalette,
                isDarkMode = settings.isDarkMode
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(androidx.wear.compose.material.MaterialTheme.colors.background),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        firebaseUser == null -> AuthScreen(
                            isFirebaseConfigured = firebaseRepository.isConfigured,
                            isLoading = authLoading,
                            errorMessage = authError,
                            onLogin = { email, password ->
                                authError = null
                                if (email.isBlank() || password.isBlank()) {
                                    authError = getString(R.string.firebase_auth_error)
                                } else {
                                    scope.launch {
                                        authLoading = true
                                        try {
                                            firebaseRepository.signIn(email, password)
                                        } catch (error: Exception) {
                                            authError = localizedError(error)
                                        } finally {
                                            authLoading = false
                                        }
                                    }
                                }
                            },
                            onRegister = { name, email, password ->
                                authError = null
                                if (password.length < 6) {
                                    authError = getString(R.string.firebase_weak_password)
                                } else {
                                    scope.launch {
                                        authLoading = true
                                        try {
                                            firebaseRepository.register(name, email, password)
                                        } catch (error: Exception) {
                                            authError = localizedError(error)
                                        } finally {
                                            authLoading = false
                                        }
                                    }
                                }
                            }
                        )

                        profileLoading -> HoneyStatusScreen(stringResource(R.string.loading_profile))

                        userProfile == null -> HoneyStatusScreen(
                            message = appError ?: stringResource(R.string.profile_unavailable),
                            actionLabel = stringResource(R.string.sign_out),
                            onAction = { firebaseRepository.signOut() }
                        )

                        userProfile?.isPaired != true -> SetupScreen(
                            pairingCode = pairingCodeOverride,
                            isGeneratingCode = codeLoading,
                            isPairing = pairingLoading,
                            errorMessage = appError,
                            onCreatePairingCode = {
                                appError = null
                                scope.launch {
                                    codeLoading = true
                                    try {
                                        pairingCodeOverride = firebaseRepository.createPairingCode()
                                    } catch (error: Exception) {
                                        appError = localizedError(error, pairing = true)
                                    } finally {
                                        codeLoading = false
                                    }
                                }
                            },
                            onPairWithCode = { code, anniversaryDate ->
                                appError = null
                                scope.launch {
                                    pairingLoading = true
                                    try {
                                        firebaseRepository.pairWithCode(code, anniversaryDate)
                                    } catch (error: Exception) {
                                        appError = localizedError(error, pairing = true)
                                    } finally {
                                        pairingLoading = false
                                    }
                                }
                            },
                            onLogout = { signOut() }
                        )

                        userProfile != null && partnerProfile == null && partnerLoading -> HoneyStatusScreen(
                            stringResource(R.string.loading_partner)
                        )

                        userProfile != null && partnerProfile == null -> HoneyStatusScreen(
                            message = appError ?: stringResource(R.string.partner_unavailable),
                            actionLabel = stringResource(R.string.sign_out),
                            onAction = { signOut() }
                        )

                        else -> when (currentRoute) {
                            ScreenRoute.HOME -> HomeScreen(
                                userProfile = userProfile!!,
                                partnerState = partnerProfile!!,
                                myMood = userProfile!!.mood,
                                daysTogether = userProfile!!.anniversaryDate?.let { anniversary ->
                                    ChronoUnit.DAYS.between(anniversary, LocalDate.now()).coerceAtLeast(0)
                                },
                                isDndActive = settings.isDndActive,
                                onNavigateToTouch = { currentRoute = ScreenRoute.TOUCH },
                                onNavigateToMessages = { currentRoute = ScreenRoute.MESSAGES },
                                onNavigateToLocation = { currentRoute = ScreenRoute.LOCATION },
                                onNavigateToMood = { currentRoute = ScreenRoute.MOOD },
                                onNavigateToSettings = { currentRoute = ScreenRoute.SETTINGS }
                            )

                            ScreenRoute.TOUCH -> TouchScreen(
                                onSendTouch = { type ->
                                    scope.launch {
                                        try {
                                            firebaseRepository.sendTouch(type)
                                            localRepository.hapticConfirmation()
                                        } catch (error: Exception) {
                                            Toast.makeText(context, localizedError(error), Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                onBack = { currentRoute = ScreenRoute.HOME }
                            )

                            ScreenRoute.MESSAGES -> QuickMessagesScreen(
                                onSendMessage = { message ->
                                    scope.launch {
                                        try {
                                            firebaseRepository.sendMessage(message)
                                            localRepository.hapticConfirmation()
                                            Toast.makeText(context, R.string.msg_sent_toast, Toast.LENGTH_SHORT).show()
                                        } catch (error: Exception) {
                                            Toast.makeText(context, localizedError(error), Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                onBack = { currentRoute = ScreenRoute.HOME }
                            )

                            ScreenRoute.LOCATION -> LocationScreen(
                                isSharingLocation = userProfile!!.isSharingLocation,
                                partnerState = partnerProfile!!,
                                partnerLocation = partnerLocation,
                                distanceMeters = distanceBetween(myLocation, partnerLocation),
                                darkMode = settings.isDarkMode,
                                languageCode = settings.languageCode,
                                onToggleSharing = { enabled ->
                                    if (enabled) requestLocationSharing()
                                    else disableLocationSharing()
                                },
                                onBack = { currentRoute = ScreenRoute.HOME }
                            )

                            ScreenRoute.MOOD -> MoodScreen(
                                currentMood = userProfile!!.mood,
                                onSelectMood = { mood ->
                                    scope.launch {
                                        try {
                                            firebaseRepository.updateMood(mood)
                                            currentRoute = ScreenRoute.HOME
                                        } catch (error: Exception) {
                                            Toast.makeText(context, localizedError(error), Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                onBack = { currentRoute = ScreenRoute.HOME }
                            )

                            ScreenRoute.SETTINGS -> SettingsScreen(
                                settings = settings,
                                onToggleDarkMode = localRepository::toggleDarkMode,
                                onToggleDnd = { enabled ->
                                    localRepository.toggleDnd(enabled)
                                    scope.launch {
                                        runCatching { firebaseRepository.setDndActive(enabled) }
                                    }
                                },
                                onToggleVibration = localRepository::toggleVibration,
                                onSelectTheme = localRepository::updateTheme,
                                onToggleLanguage = {
                                    localRepository.updateLanguage(if (settings.languageCode == "es") "en" else "es")
                                    recreate()
                                },
                                onLogout = { signOut() },
                                onBack = { currentRoute = ScreenRoute.HOME }
                            )
                        }
                    }

                    receivedTouch?.let { touch ->
                        TouchReactionOverlay(touchType = touch, onDismiss = { receivedTouch = null })
                    }
                }
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationSharing() {
        if (hasLocationPermission()) {
            enableLocationSharing()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun enableLocationSharing() {
        lifecycleScope.launch {
            try {
                firebaseRepository.setLocationSharing(true)
                startLocationShareService()
            } catch (error: Exception) {
                Toast.makeText(this@MainActivity, localizedError(error), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun disableLocationSharing() {
        stopLocationShareService()
        lifecycleScope.launch {
            try {
                firebaseRepository.setLocationSharing(false)
            } catch (error: Exception) {
                Toast.makeText(this@MainActivity, localizedError(error), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startLocationShareService() {
        if (!hasLocationPermission()) return
        ContextCompat.startForegroundService(
            this,
            Intent(this, LocationShareService::class.java).setAction(LocationShareService.ACTION_START)
        )
    }

    private fun stopLocationShareService() {
        stopService(Intent(this, LocationShareService::class.java))
    }

    private fun signOut() {
        stopLocationShareService()
        lifecycleScope.launch {
            runCatching { firebaseRepository.setLocationSharing(false) }
            firebaseRepository.signOut()
        }
    }

    private fun distanceBetween(first: SharedLocation?, second: SharedLocation?): Int? {
        if (first == null || second == null) return null
        val result = FloatArray(1)
        Location.distanceBetween(
            first.latitude,
            first.longitude,
            second.latitude,
            second.longitude,
            result
        )
        return result[0].toInt()
    }

    private fun localizedError(error: Throwable, pairing: Boolean = false): String = when (error) {
        is FirebaseNetworkException -> getString(R.string.firebase_network_error)
        is FirebaseFunctionsException -> getString(
            if (pairing) R.string.firebase_pairing_error else R.string.firebase_generic_error
        )
        is FirebaseAuthException -> when (error.errorCode) {
            "ERROR_EMAIL_ALREADY_IN_USE" -> getString(R.string.firebase_email_in_use)
            "ERROR_WEAK_PASSWORD" -> getString(R.string.firebase_weak_password)
            "ERROR_INVALID_EMAIL" -> getString(R.string.firebase_invalid_email)
            else -> getString(R.string.firebase_auth_error)
        }
        else -> getString(if (pairing) R.string.firebase_pairing_error else R.string.firebase_generic_error)
    }
}

@androidx.compose.runtime.Composable
private fun HoneyStatusScreen(
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    androidx.wear.compose.material.ScalingLazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        item {
            androidx.wear.compose.material.Text(
                text = message,
                color = androidx.wear.compose.material.MaterialTheme.colors.onSurface,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
        if (actionLabel != null && onAction != null) {
            item {
                androidx.wear.compose.material.Button(onClick = onAction) {
                    androidx.wear.compose.material.Text(actionLabel)
                }
            }
        }
    }
}
