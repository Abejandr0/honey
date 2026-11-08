package com.honey.wear.ui.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

@Composable
fun HoneyWearTheme(
    selectedTheme: HoneyThemePalette = HoneyThemePalette.HONEY,
    isDarkMode: Boolean = true,
    content: @Composable () -> Unit
) {
    val wearColors = if (isDarkMode) {
        Colors(
            primary = selectedTheme.primary,
            primaryVariant = selectedTheme.glow,
            secondary = selectedTheme.primary,
            background = DarkOledBackground,
            surface = DarkOledSurface,
            onPrimary = DarkOledBackground,
            onSecondary = DarkOledBackground,
            onBackground = DarkOledTextPrimary,
            onSurface = DarkOledTextPrimary
        )
    } else {
        Colors(
            primary = selectedTheme.primary,
            primaryVariant = selectedTheme.glow,
            secondary = selectedTheme.primary,
            background = LightBackground,
            surface = LightSurface,
            onPrimary = LightBackground,
            onSecondary = LightBackground,
            onBackground = LightTextPrimary,
            onSurface = LightTextPrimary
        )
    }

    MaterialTheme(
        colors = wearColors,
        content = content
    )
}
