package com.honey.wear.data

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.honey.wear.ui.theme.HoneyThemePalette
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

class HoneyRepository(private val context: Context) {

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    // User Profile & Authentication State
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Partner State
    private val _partnerState = MutableStateFlow(PartnerState())
    val partnerState: StateFlow<PartnerState> = _partnerState.asStateFlow()

    // Settings State
    private val _settings = MutableStateFlow(HoneySettings())
    val settings: StateFlow<HoneySettings> = _settings.asStateFlow()

    // My current mood
    private val _myMood = MutableStateFlow(MoodType.HAPPY)
    val myMood: StateFlow<MoodType> = _myMood.asStateFlow()

    // My current GPS Location
    private val _myLatitude = MutableStateFlow(4.6090)
    private val _myLongitude = MutableStateFlow(-74.0820)
    val myLatitude: StateFlow<Double> = _myLatitude.asStateFlow()
    val myLongitude: StateFlow<Double> = _myLongitude.asStateFlow()

    private val _isSharingMyLocation = MutableStateFlow(true)
    val isSharingMyLocation: StateFlow<Boolean> = _isSharingMyLocation.asStateFlow()

    // Received Touch Reaction Overlay Trigger
    private val _receivedTouch = MutableStateFlow<TouchType?>(null)
    val receivedTouch: StateFlow<TouchType?> = _receivedTouch.asStateFlow()

    // DND Silent Queue
    private val _dndQueuedItems = MutableStateFlow<List<QueuedInteraction>>(emptyList())
    val dndQueuedItems: StateFlow<List<QueuedInteraction>> = _dndQueuedItems.asStateFlow()

    // UI Toast / Feedback message
    private val _feedbackToast = MutableStateFlow<String?>(null)
    val feedbackToast: StateFlow<String?> = _feedbackToast.asStateFlow()

    // --- Authentication Actions ---
    fun login(pin: String): Boolean {
        if (pin.length >= 4) {
            _userProfile.value = _userProfile.value.copy(
                username = "Abejandro",
                isAuthenticated = true
            )
            return true
        }
        return false
    }

    fun register(username: String, pin: String): Boolean {
        if (username.isNotBlank() && pin.length >= 4) {
            _userProfile.value = _userProfile.value.copy(
                username = username,
                isAuthenticated = true
            )
            return true
        }
        return false
    }

    fun completePairing(partnerCode: String, anniversary: LocalDate, avatar: String) {
        _userProfile.value = _userProfile.value.copy(
            partnerPairingCode = partnerCode.ifBlank { "HNY-1337" },
            anniversaryDate = anniversary,
            avatarEmoji = avatar,
            isPaired = true
        )
    }

    fun logout() {
        _userProfile.value = UserProfile(isAuthenticated = false, isPaired = false)
    }

    // --- Days Together Calculation ---
    fun getDaysTogether(): Long {
        val start = _userProfile.value.anniversaryDate
        val now = LocalDate.now()
        val days = ChronoUnit.DAYS.between(start, now)
        return if (days >= 0) days else 0
    }

    // --- Touch / Cariño Interactions ---
    fun sendTouch(type: TouchType) {
        if (_partnerState.value.isDndActive) {
            _dndQueuedItems.value = _dndQueuedItems.value + QueuedInteraction("touch", type.name)
            _feedbackToast.value = "🌙 Pareja en No Molestar (guardado)"
            return
        }

        // Trigger native watch vibration
        performHapticTouch(type)
        _feedbackToast.value = "¡${type.name} enviado! ${type.emoji}"
    }

    fun simulateIncomingTouch(type: TouchType) {
        if (_settings.value.isDndActive) {
            _dndQueuedItems.value = _dndQueuedItems.value + QueuedInteraction("touch", type.name)
            return
        }
        _receivedTouch.value = type
        performHapticTouch(type)
    }

    fun dismissIncomingTouch() {
        _receivedTouch.value = null
    }

    private fun performHapticTouch(type: TouchType) {
        if (!_settings.value.isVibrationEnabled || !vibrator.hasVibrator()) return

        when (type) {
            TouchType.CARINO -> {
                // Heartbeat rhythm: thump-thump
                val timings = longArrayOf(0, 70, 80, 110)
                val amplitudes = intArrayOf(0, 180, 0, 255)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            }
            TouchType.ABRAZO -> {
                // Warm, prolonged, deep embrace
                val timings = longArrayOf(0, 400)
                val amplitudes = intArrayOf(0, 200)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            }
            TouchType.BESO -> {
                // Playful, sparkling kiss pattern
                val timings = longArrayOf(0, 60, 40, 60, 40, 140)
                val amplitudes = intArrayOf(0, 150, 0, 180, 0, 240)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            }
        }
    }

    // --- Quick Messages ---
    fun sendQuickMessage(text: String) {
        if (_partnerState.value.isDndActive) {
            _dndQueuedItems.value = _dndQueuedItems.value + QueuedInteraction("message", text)
            _feedbackToast.value = "🌙 Pareja en No Molestar (guardado)"
            return
        }
        if (_settings.value.isVibrationEnabled) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        }
        _feedbackToast.value = "Mensaje enviado ✓"
    }

    // --- GPS Location Sharing ---
    fun updateMyLocation(lat: Double, lng: Double) {
        _myLatitude.value = lat
        _myLongitude.value = lng
    }

    fun toggleLocationSharing(share: Boolean) {
        _isSharingMyLocation.value = share
    }

    fun calculateDistanceToPartnerMeters(): Int {
        val lat1 = _myLatitude.value
        val lon1 = _myLongitude.value
        val lat2 = _partnerState.value.latitude
        val lon2 = _partnerState.value.longitude

        val earthRadius = 6371000.0 // meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (earthRadius * c).roundToInt()
    }

    // --- Mood Tracker ---
    fun updateMyMood(mood: MoodType) {
        _myMood.value = mood
        _feedbackToast.value = "Ánimo actualizado ${mood.emoji}"
    }

    // --- Settings Updates ---
    fun updateTheme(palette: HoneyThemePalette) {
        _settings.value = _settings.value.copy(themePalette = palette)
    }

    fun toggleDarkMode(isDark: Boolean) {
        _settings.value = _settings.value.copy(isDarkMode = isDark)
    }

    fun toggleDnd(isActive: Boolean) {
        _settings.value = _settings.value.copy(isDndActive = isActive)
        if (!isActive && _dndQueuedItems.value.isNotEmpty()) {
            val count = _dndQueuedItems.value.size
            _dndQueuedItems.value = emptyList()
            _feedbackToast.value = "🌙 $count mensajes/toques recibidos mientras dormías"
        }
    }

    fun toggleVibration(isEnabled: Boolean) {
        _settings.value = _settings.value.copy(isVibrationEnabled = isEnabled)
        if (isEnabled) {
            vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    fun updateLanguage(lang: String) {
        _settings.value = _settings.value.copy(languageCode = lang)
    }

    fun clearFeedbackToast() {
        _feedbackToast.value = null
    }
}
