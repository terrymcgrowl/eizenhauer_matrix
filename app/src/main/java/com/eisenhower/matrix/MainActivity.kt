package com.eisenhower.matrix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.eisenhower.matrix.ui.screens.MatrixScreen
import com.eisenhower.matrix.ui.screens.MatrixViewModel
import com.eisenhower.matrix.ui.theme.EisenhowerTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Единственная Activity приложения.
 * Использует Jetpack Compose для всего UI.
 * @AndroidEntryPoint — Hilt внедряет зависимости в Activity.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // ViewModel жива пока живёт Activity (или дольше при смене конфигурации)
    private val viewModel: MatrixViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge: контент рисуется под статус-баром и навигацией
        enableEdgeToEdge()

        setContent {
            // Следим за настройкой темы из DataStore
            val uiState by viewModel.uiState.collectAsState()

            EisenhowerTheme(themePreference = uiState.themePreference) {
                MatrixScreen(viewModel = viewModel)
            }
        }
    }
}
