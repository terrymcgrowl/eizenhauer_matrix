package com.eisenhower.matrix.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Минималистичная палитра — только то, что нужно
// Акценты квадрантов: приглушённые, не кричащие
val AccentRed    = Color(0xFFD94F4F)  // Q1
val AccentBlue   = Color(0xFF4A7CC7)  // Q2
val AccentAmber  = Color(0xFFD4832A)  // Q3
val AccentGray   = Color(0xFF8A8A8A)  // Q4

val DividerColor     = Color(0xFFE0E0E0)
val DividerColorDark = Color(0xFF2C2C2C)

val LightColorScheme = lightColorScheme(
    primary          = Color(0xFF1A1A1A),
    onPrimary        = Color.White,
    background       = Color(0xFFF7F7F7),
    onBackground     = Color(0xFF1A1A1A),
    surface          = Color.White,
    onSurface        = Color(0xFF1A1A1A),
    surfaceVariant   = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFF6B6B6B),
    outline          = Color(0xFFE0E0E0),
    error            = Color(0xFFD94F4F),
)

val DarkColorScheme = darkColorScheme(
    primary          = Color(0xFFF0F0F0),
    onPrimary        = Color(0xFF1A1A1A),
    background       = Color(0xFF111111),
    onBackground     = Color(0xFFEEEEEE),
    surface          = Color(0xFF1C1C1C),
    onSurface        = Color(0xFFEEEEEE),
    surfaceVariant   = Color(0xFF242424),
    onSurfaceVariant = Color(0xFF909090),
    outline          = Color(0xFF2C2C2C),
    error            = Color(0xFFD94F4F),
)