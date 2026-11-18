package com.honey.wear.ui.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

@Composable
fun HoneyWearTheme(
    selectedTheme: HoneyThemePalette = HoneyThemePalette.HONEY,
    isDarkMode: Boolean = true,
    content: @Composable () -> Unit
) {
    val accentText = if (selectedTheme.primary.luminance() > 0.48f) {
        Color(0xFF21180D)
    } else {
        Color.White
    }
    val wearColors = if (isDarkMode) {
        Colors(
            primary = selectedTheme.primary,
            primaryVariant = selectedTheme.glow,
            secondary = selectedTheme.primary,
            background = DarkOledBackground,
            surface = DarkOledSurface,
            onPrimary = accentText,
            onSecondary = accentText,
            onBackground = DarkOledTextPrimary,
            onSurface = DarkOledTextPrimary,
            error = Color(0xFFFF7187),
            onError = Color(0xFF260710)
        )
    } else {
        Colors(
            primary = selectedTheme.primary,
            primaryVariant = selectedTheme.glow,
            secondary = selectedTheme.primary,
            background = LightBackground,
            surface = LightSurface,
            onPrimary = accentText,
            onSecondary = accentText,
            onBackground = LightTextPrimary,
            onSurface = LightTextPrimary,
            error = Color(0xFFB4233B),
            onError = Color.White
        )
    }

    MaterialTheme(
        colors = wearColors,
        content = content
    )
}
