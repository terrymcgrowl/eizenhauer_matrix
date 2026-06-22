package com.eisenhower.matrix.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.eisenhower.matrix.data.model.Quadrant

data class QuadrantColors(
    val accent: Color,
    val background: Color
)

@Composable
fun Quadrant.colors(): QuadrantColors {
    val isDark = isSystemInDarkTheme()
    return when (this) {
        Quadrant.DO_FIRST -> QuadrantColors(
            accent = AccentRed,
            background = if (isDark) Color(0xFF1C1C1C) else Color.White
        )
        Quadrant.SCHEDULE -> QuadrantColors(
            accent = AccentBlue,
            background = if (isDark) Color(0xFF1C1C1C) else Color.White
        )
        Quadrant.DELEGATE -> QuadrantColors(
            accent = AccentAmber,
            background = if (isDark) Color(0xFF1C1C1C) else Color.White
        )
        Quadrant.ELIMINATE -> QuadrantColors(
            accent = AccentGray,
            background = if (isDark) Color(0xFF1C1C1C) else Color.White
        )
    }
}