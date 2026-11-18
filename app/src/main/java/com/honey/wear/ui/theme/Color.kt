package com.honey.wear.ui.theme

import com.honey.wear.R
import androidx.compose.ui.graphics.Color

// 8 Theme Accent Colors
val ThemeBlue = Color(0xFF2563EB)      // Azul Zafiro
val ThemePink = Color(0xFFEC4899)      // Rosado Dulce
val ThemePurple = Color(0xFF8B5CF6)    // Morado Cyber
val ThemeGreen = Color(0xFF10B981)     // Verde Esmeralda
val ThemeHoney = Color(0xFFF59E0B)     // Amarillo Miel (Default)
val ThemeCoral = Color(0xFFFF6B6B)     // Coral Atardecer
val ThemeCyan = Color(0xFF06B6D4)      // Neón Turquesa
val ThemeRuby = Color(0xFFE11D48)      // Rojo Rubí

enum class HoneyThemePalette(val labelRes: Int, val primary: Color, val glow: Color) {
    HONEY(R.string.theme_honey, ThemeHoney, Color(0x55F59E0B)),
    BLUE(R.string.theme_blue, ThemeBlue, Color(0x552563EB)),
    PINK(R.string.theme_pink, ThemePink, Color(0x55EC4899)),
    PURPLE(R.string.theme_purple, ThemePurple, Color(0x558B5CF6)),
    GREEN(R.string.theme_green, ThemeGreen, Color(0x5510B981)),
    CORAL(R.string.theme_coral, ThemeCoral, Color(0x55FF6B6B)),
    CYAN(R.string.theme_cyan, ThemeCyan, Color(0x5506B6D4)),
    RUBY(R.string.theme_ruby, ThemeRuby, Color(0x55E11D48))
}

// Dark OLED Palette (True Black for OLED battery saving)
val DarkOledBackground = Color(0xFF080A0F)
val DarkOledSurface = Color(0xFF171A25)
val DarkOledTextPrimary = Color(0xFFF7F5FA)
val DarkOledTextSecondary = Color(0xFFB6B2C2)

// Clean Pearl Light Palette
val LightBackground = Color(0xFFF5F3F8)
val LightSurface = Color(0xFFEAE7F0)
val LightTextPrimary = Color(0xFF191722)
val LightTextSecondary = Color(0xFF625E6B)
