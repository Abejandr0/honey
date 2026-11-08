package com.honey.wear

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.honey.wear.data.HoneyRepository
import com.honey.wear.ui.components.TouchReactionOverlay
import com.honey.wear.ui.screens.*
import com.honey.wear.ui.theme.HoneyWearTheme

enum class ScreenRoute {
    HOME,
    TOUCH,
    MESSAGES,
    LOCATION,
    MOOD,
    SETTINGS
}

class MainActivity : ComponentActivity() {

    private lateinit var repository: HoneyRepository
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            startLocationUpdates()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = HoneyRepository(applicationContext)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupLocationUpdates()
        checkLocationPermissions()

        setContent {
            val userProfile by repository.userProfile.collectAsState()
            val partnerState by repository.partnerState.collectAsState()
            val settings by repository.settings.collectAsState()
            val myMood by repository.myMood.collectAsState()
            val isSharingLocation by repository.isSharingMyLocation.collectAsState()
            val receivedTouch by repository.receivedTouch.collectAsState()
            val feedbackToast by repository.feedbackToast.collectAsState()

            var currentRoute by remember { mutableStateOf(ScreenRoute.HOME) }

            // Show feedback toast when triggered
            LaunchedEffect(feedbackToast) {
                feedbackToast?.let {
                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                    repository.clearFeedbackToast()
                }
            }

            HoneyWearTheme(
                selectedTheme = settings.themePalette,
                isDarkMode = settings.isDarkMode
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Navigation Flow
                    when {
                        // 1. Mandatory Auth Screen
                        !userProfile.isAuthenticated -> {
                            AuthScreen(
                                onAuthSuccess = { /* Automatically transitions */ },
                                onLogin = { pin -> repository.login(pin) },
                                onRegister = { user, pin -> repository.register(user, pin) }
                            )
                        }

                        // 2. Initial Setup & Partner Match
                        !userProfile.isPaired -> {
                            SetupScreen(
                                myPairingCode = userProfile.myPairingCode,
                                onSetupCompleted = { partnerCode, date, avatar ->
                                    repository.completePairing(partnerCode, date, avatar)
                                }
                            )
                        }

                        // 3. Main App Screens
                        else -> {
                            when (currentRoute) {
                                ScreenRoute.HOME -> {
                                    HomeScreen(
                                        userProfile = userProfile,
                                        partnerState = partnerState,
                                        myMood = myMood,
                                        daysTogether = repository.getDaysTogether(),
                                        isDndActive = settings.isDndActive,
                                        onNavigateToTouch = { currentRoute = ScreenRoute.TOUCH },
                                        onNavigateToMessages = { currentRoute = ScreenRoute.MESSAGES },
                                        onNavigateToLocation = { currentRoute = ScreenRoute.LOCATION },
                                        onNavigateToMood = { currentRoute = ScreenRoute.MOOD },
                                        onNavigateToSettings = { currentRoute = ScreenRoute.SETTINGS }
                                    )
                                }
                                ScreenRoute.TOUCH -> {
                                    TouchScreen(
                                        onSendTouch = { type -> repository.sendTouch(type) },
                                        onSimulateIncoming = { type -> repository.simulateIncomingTouch(type) },
                                        onBack = { currentRoute = ScreenRoute.HOME }
                                    )
                                }
                                ScreenRoute.MESSAGES -> {
                                    QuickMessagesScreen(
                                        onSendMessage = { text -> repository.sendQuickMessage(text) },
                                        onBack = { currentRoute = ScreenRoute.HOME }
                                    )
                                }
                                ScreenRoute.LOCATION -> {
                                    LocationScreen(
                                        isSharingLocation = isSharingLocation,
                                        distanceMeters = repository.calculateDistanceToPartnerMeters(),
                                        partnerState = partnerState,
                                        onToggleSharing = { share -> repository.toggleLocationSharing(share) },
                                        onRefreshLocation = { startLocationUpdates() },
                                        onBack = { currentRoute = ScreenRoute.HOME }
                                    )
                                }
                                ScreenRoute.MOOD -> {
                                    MoodScreen(
                                        currentMood = myMood,
                                        onSelectMood = { mood ->
                                            repository.updateMyMood(mood)
                                            currentRoute = ScreenRoute.HOME
                                        },
                                        onBack = { currentRoute = ScreenRoute.HOME }
                                    )
                                }
                                ScreenRoute.SETTINGS -> {
                                    SettingsScreen(
                                        settings = settings,
                                        onToggleDarkMode = { isDark -> repository.toggleDarkMode(isDark) },
                                        onToggleDnd = { isDnd -> repository.toggleDnd(isDnd) },
                                        onToggleVibration = { vib -> repository.toggleVibration(vib) },
                                        onSelectTheme = { theme -> repository.updateTheme(theme) },
                                        onToggleLanguage = {
                                            val nextLang = if (settings.languageCode == "es") "en" else "es"
                                            repository.updateLanguage(nextLang)
                                        },
                                        onLogout = {
                                            repository.logout()
                                            currentRoute = ScreenRoute.HOME
                                        },
                                        onBack = { currentRoute = ScreenRoute.HOME }
                                    )
                                }
                            }
                        }
                    }

                    // Received Touch Fullscreen Reaction Overlay
                    receivedTouch?.let { touchType ->
                        TouchReactionOverlay(
                            touchType = touchType,
                            onDismiss = { repository.dismissIncomingTouch() }
                        )
                    }
                }
            }
        }
    }

    private fun checkLocationPermissions() {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (fineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun setupLocationUpdates() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { loc ->
                    repository.updateMyLocation(loc.latitude, loc.longitude)
                }
            }
        }
    }

    private fun startLocationUpdates() {
        val finePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (finePermission != PackageManager.PERMISSION_GRANTED) return

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 5000
        ).setMinUpdateIntervalMillis(3000).build()

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            // Permission check fallback
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
