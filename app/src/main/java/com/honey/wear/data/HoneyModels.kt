package com.honey.wear.data

import com.honey.wear.R
import com.honey.wear.ui.theme.HoneyThemePalette
import java.time.LocalDate

enum class TouchType(val titleRes: Int, val descRes: Int) {
    CARINO(R.string.mode_carino, R.string.desc_carino),
    ABRAZO(R.string.mode_abrazo, R.string.desc_abrazo),
    BESO(R.string.mode_beso, R.string.desc_beso)
}

enum class MoodType(val labelRes: Int) {
    HAPPY(R.string.mood_happy),
    IN_LOVE(R.string.mood_in_love),
    TIRED(R.string.mood_tired),
    CUDDLE(R.string.mood_cuddle),
    STRESSED(R.string.mood_stressed),
    PLAYFUL(R.string.mood_playful)
}

data class UserProfile(
    val uid: String,
    val username: String,
    val pairingCode: String? = null,
    val partnerId: String? = null,
    val coupleId: String? = null,
    val anniversaryDate: LocalDate? = null,
    val mood: MoodType? = null,
    val isSharingLocation: Boolean = false
) {
    val isPaired: Boolean get() = !coupleId.isNullOrBlank() && !partnerId.isNullOrBlank()
}

data class PartnerState(
    val uid: String,
    val name: String,
    val mood: MoodType?,
    val isSharingLocation: Boolean,
    val isDndActive: Boolean = false
)

data class SharedLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float?,
    val updatedAtMillis: Long
)

data class HoneyMessage(
    val id: String,
    val senderUid: String,
    val kind: String,
    val text: String? = null,
    val touchType: TouchType? = null,
    val createdAtMillis: Long = 0L
)

data class HoneySettings(
    val languageCode: String = "es",
    val themePalette: HoneyThemePalette = HoneyThemePalette.HONEY,
    val isDarkMode: Boolean = true,
    val isDndActive: Boolean = false,
    val isVibrationEnabled: Boolean = true
)
