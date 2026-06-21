package com.eisenhower.matrix.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.eisenhower.matrix.data.model.Quadrant

/**
 * Цветовые данные для квадранта.
 * @param accent Основной цвет квадранта (иконка, рамка)
 * @param background Фоновый цвет карточек/квадранта
 * @param headerBackground Фон заголовка квадранта
 */
data class QuadrantColors(
    val accent: Color,
    val background: Color,
    val headerBackground: Color
)

/**
 * Возвращает цветовую схему для каждого квадранта с учётом текущей темы.
 */
@Composable
fun Quadrant.colors(): QuadrantColors {
    val isDark = isSystemInDarkTheme()
    return when (this) {
        Quadrant.DO_FIRST -> QuadrantColors(
            accent = QuadrantDoFirst,
            background = if (isDark) QuadrantDoFirstDark else QuadrantDoFirstLight,
            headerBackground = if (isDark) Color(0xFF3A0000) else Color(0xFFFFCDD2)
        )
        Quadrant.SCHEDULE -> QuadrantColors(
            accent = QuadrantSchedule,
            background = if (isDark) QuadrantScheduleDark else QuadrantScheduleLight,
            headerBackground = if (isDark) Color(0xFF0A1E3A) else Color(0xFFBBDEFB)
        )
        Quadrant.DELEGATE -> QuadrantColors(
            accent = QuadrantDelegate,
            background = if (isDark) QuadrantDelegateDark else QuadrantDelegateLight,
            headerBackground = if (isDark) Color(0xFF3A1A00) else Color(0xFFFFE0B2)
        )
        Quadrant.ELIMINATE -> QuadrantColors(
            accent = QuadrantEliminate,
            background = if (isDark) QuadrantEliminateDark else QuadrantEliminateLight,
            headerBackground = if (isDark) Color(0xFF2A2A2A) else Color(0xFFEEEEEE)
        )
    }
}

/**
 * Возвращает иконку-эмодзи для каждого квадранта.
 */
fun Quadrant.icon(): String = when (this) {
    Quadrant.DO_FIRST -> "🔥"
    Quadrant.SCHEDULE -> "📅"
    Quadrant.DELEGATE -> "🤝"
    Quadrant.ELIMINATE -> "🗑️"
}
