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

/** Local device preferences and haptic output. Couple data lives in FirebaseHoneyRepository. */
class HoneyRepository(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    private val _settings = MutableStateFlow(readSettings())
    val settings: StateFlow<HoneySettings> = _settings.asStateFlow()

    fun updateTheme(palette: HoneyThemePalette) = updateSettings(
        _settings.value.copy(themePalette = palette)
    )

    fun toggleDarkMode(enabled: Boolean) = updateSettings(
        _settings.value.copy(isDarkMode = enabled)
    )

    fun toggleDnd(enabled: Boolean) = updateSettings(
        _settings.value.copy(isDndActive = enabled)
    )

    fun toggleVibration(enabled: Boolean) {
        updateSettings(_settings.value.copy(isVibrationEnabled = enabled))
        if (enabled) hapticConfirmation()
    }

    fun updateLanguage(languageCode: String) = updateSettings(
        _settings.value.copy(languageCode = languageCode)
    )

    fun performHapticTouch(type: TouchType) {
        if (!_settings.value.isVibrationEnabled || !vibrator.hasVibrator()) return

        val (timings, amplitudes) = when (type) {
            TouchType.CARINO -> longArrayOf(0, 70, 80, 110) to intArrayOf(0, 180, 0, 255)
            TouchType.ABRAZO -> longArrayOf(0, 400) to intArrayOf(0, 200)
            TouchType.BESO -> longArrayOf(0, 60, 40, 60, 40, 140) to intArrayOf(0, 150, 0, 180, 0, 240)
        }
        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
    }

    fun hapticConfirmation() {
        if (_settings.value.isVibrationEnabled && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(90, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    private fun updateSettings(settings: HoneySettings) {
        _settings.value = settings
        preferences.edit()
            .putString(KEY_LANGUAGE, settings.languageCode)
            .putString(KEY_THEME, settings.themePalette.name)
            .putBoolean(KEY_DARK, settings.isDarkMode)
            .putBoolean(KEY_DND, settings.isDndActive)
            .putBoolean(KEY_VIBRATION, settings.isVibrationEnabled)
            .apply()
    }

    private fun readSettings(): HoneySettings {
        val language = preferences.getString(KEY_LANGUAGE, "es")
            ?.takeIf { it == "es" || it == "en" } ?: "es"
        val theme = preferences.getString(KEY_THEME, HoneyThemePalette.HONEY.name)
            ?.let { runCatching { HoneyThemePalette.valueOf(it) }.getOrNull() }
            ?: HoneyThemePalette.HONEY
        return HoneySettings(
            languageCode = language,
            themePalette = theme,
            isDarkMode = preferences.getBoolean(KEY_DARK, true),
            isDndActive = preferences.getBoolean(KEY_DND, false),
            isVibrationEnabled = preferences.getBoolean(KEY_VIBRATION, true)
        )
    }

    private companion object {
        const val PREFERENCES = "honey_preferences"
        const val KEY_LANGUAGE = "language"
        const val KEY_THEME = "theme"
        const val KEY_DARK = "dark_mode"
        const val KEY_DND = "do_not_disturb"
        const val KEY_VIBRATION = "vibration"
    }
}
