package com.eisenhower.matrix.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ============================================================
// Цветовая система матрицы Эйзенхауэра
// ============================================================

// --- Цвета квадрантов ---
// Q1: Срочно + Важно → красный (тревога, немедленное действие)
val QuadrantDoFirst = Color(0xFFE53935)
val QuadrantDoFirstLight = Color(0xFFFFEBEE)
val QuadrantDoFirstDark = Color(0xFF4A0000)

// Q2: Важно + Не срочно → синий (стратегия, развитие)
val QuadrantSchedule = Color(0xFF1E88E5)
val QuadrantScheduleLight = Color(0xFFE3F2FD)
val QuadrantScheduleDark = Color(0xFF0A2744)

// Q3: Срочно + Не важно → жёлтый (делегирование)
val QuadrantDelegate = Color(0xFFFB8C00)
val QuadrantDelegateLight = Color(0xFFFFF3E0)
val QuadrantDelegateDark = Color(0xFF4A2500)

// Q4: Не срочно + Не важно → серый (устранение)
val QuadrantEliminate = Color(0xFF757575)
val QuadrantEliminateLight = Color(0xFFF5F5F5)
val QuadrantEliminateDark = Color(0xFF212121)

// --- Базовые цвета приложения ---
val Primary = Color(0xFF2E3A59)          // Тёмно-синий — заголовки, акценты
val PrimaryDark = Color(0xFF90CAF9)      // Светло-синий для тёмной темы
val Surface = Color(0xFFF8F9FA)          // Очень светлый фон
val SurfaceDark = Color(0xFF1C1B1F)      // Тёмный фон
val DividerColor = Color(0xFFBDBDBD)     // Разделители квадрантов
val DividerColorDark = Color(0xFF424242)

// --- Светлая тема ---
val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8EAF6),
    onPrimaryContainer = Primary,
    secondary = Color(0xFF546E7A),
    onSecondary = Color.White,
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A1A2E),
    surface = Color.White,
    onSurface = Color(0xFF1A1A2E),
    surfaceVariant = Color(0xFFF0F2F5),
    onSurfaceVariant = Color(0xFF44474F),
    outline = Color(0xFFE0E0E0),
    error = Color(0xFFB00020),
)

// --- Тёмная тема ---
val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = Color(0xFF00315C),
    primaryContainer = Color(0xFF004784),
    onPrimaryContainer = Color(0xFFD3E4FF),
    secondary = Color(0xFF90A4AE),
    onSecondary = Color(0xFF1A3540),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1E1E2E),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF252535),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF333344),
    error = Color(0xFFCF6679),
)
