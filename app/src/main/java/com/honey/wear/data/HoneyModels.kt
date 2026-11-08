package com.honey.wear.data

import com.honey.wear.R
import com.honey.wear.ui.theme.HoneyThemePalette
import java.time.LocalDate

enum class TouchType(val titleRes: Int, val descRes: Int, val emoji: String) {
    CARINO(R.string.mode_carino, R.string.desc_carino, "💗"),
    ABRAZO(R.string.mode_abrazo, R.string.desc_abrazo, "🤗"),
    BESO(R.string.mode_beso, R.string.desc_beso, "😘")
}

enum class MoodType(val emoji: String, val labelRes: Int) {
    HAPPY("😊", R.string.mood_happy),
    IN_LOVE("🥰", R.string.mood_in_love),
    TIRED("😴", R.string.mood_tired),
    CUDDLE("🥺", R.string.mood_cuddle),
    STRESSED("🤯", R.string.mood_stressed),
    PLAYFUL("😏", R.string.mood_playful)
}

data class UserProfile(
    val username: String = "",
    val avatarEmoji: String = "🌸",
    val myPairingCode: String = "HNY-8492",
    val partnerPairingCode: String = "",
    val anniversaryDate: LocalDate = LocalDate.of(2024, 2, 14),
    val isAuthenticated: Boolean = false,
    val isPaired: Boolean = false
)

data class PartnerState(
    val name: String = "Mi Amor",
    val avatarEmoji: String = "🐻",
    val currentMood: MoodType = MoodType.IN_LOVE,
    val isSharingLocation: Boolean = true,
    val latitude: Double = 4.6097,
    val longitude: Double = -74.0817,
    val isDndActive: Boolean = false
)

data class QueuedInteraction(
    val type: String, // "touch" or "message"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class HoneySettings(
    val languageCode: String = "es",
    val themePalette: HoneyThemePalette = HoneyThemePalette.HONEY,
    val isDarkMode: Boolean = true,
    val isDndActive: Boolean = false,
    val isVibrationEnabled: Boolean = true
)
