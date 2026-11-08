package com.honey.wear.ui.theme

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

enum class HoneyThemePalette(val displayName: String, val primary: Color, val glow: Color) {
    HONEY("Miel", ThemeHoney, Color(0x55F59E0B)),
    BLUE("Azul", ThemeBlue, Color(0x552563EB)),
    PINK("Rosado", ThemePink, Color(0x55EC4899)),
    PURPLE("Morado", ThemePurple, Color(0x558B5CF6)),
    GREEN("Verde", ThemeGreen, Color(0x5510B981)),
    CORAL("Coral", ThemeCoral, Color(0x55FF6B6B)),
    CYAN("Neón", ThemeCyan, Color(0x5506B6D4)),
    RUBY("Rubí", ThemeRuby, Color(0x55E11D48))
}

// Dark OLED Palette (True Black for OLED battery saving)
val DarkOledBackground = Color(0xFF000000)
val DarkOledSurface = Color(0xFF141824)
val DarkOledCard = Color(0x1AFFFFFF)
val DarkOledTextPrimary = Color(0xFFF8FAFC)
val DarkOledTextSecondary = Color(0xFF94A3B8)

// Clean Pearl Light Palette
val LightBackground = Color(0xFFF1F5F9)
val LightSurface = Color(0xFFE2E8F0)
val LightCard = Color(0x0F000000)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF64748B)
