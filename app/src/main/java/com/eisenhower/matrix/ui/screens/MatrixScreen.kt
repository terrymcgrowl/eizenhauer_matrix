package com.eisenhower.matrix.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eisenhower.matrix.data.model.Quadrant
import com.eisenhower.matrix.ui.components.*
import com.eisenhower.matrix.ui.theme.DividerColor
import com.eisenhower.matrix.utils.ThemePreference

/**
 * Главный экран — матрица Эйзенхауэра 2×2.
 * Собирает состояние из ViewModel и отображает все 4 квадранта.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatrixScreen(
    viewModel: MatrixViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // DragDropContainer — корневой контейнер для drag & drop
    DragDropContainer(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            topBar = {
                MatrixTopBar(
                    themePreference = uiState.themePreference,
                    onThemeChange = viewModel::setTheme
                )
            }
        ) { paddingValues ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Сетка матрицы 2x2
                MatrixGrid(
                    uiState = uiState,
                    onAddTask = viewModel::showAddDialog,
                    onDragStart = viewModel::onDragStart,
                    onDragCancel = viewModel::onDragCancel,
                    onDrop = viewModel::onDrop,
                    onHover = viewModel::onDragHover,
                    onDeleteRequest = viewModel::requestDelete,
                    onEditRequest = viewModel::showEditDialog
                )

                // Туториал при первом запуске
                AnimatedVisibility(
                    visible = uiState.showTutorial,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    TutorialOverlay(onDismiss = viewModel::dismissTutorial)
                }
            }
        }

        // Диалог добавления задачи
        if (uiState.showAddDialog && uiState.addToQuadrant != null) {
            AddTaskDialog(
                quadrant = uiState.addToQuadrant!!,
                onConfirm = { title, desc ->
                    viewModel.addTask(title, desc, uiState.addToQuadrant!!)
                },
                onDismiss = viewModel::dismissAddDialog
            )
        }

        // Диалог редактирования задачи
        uiState.taskToEdit?.let { task ->
            EditTaskDialog(
                task = task,
                onConfirm = { title, desc -> viewModel.updateTask(task, title, desc) },
                onDismiss = viewModel::dismissEditDialog
            )
        }

        // Диалог подтверждения удаления
        uiState.taskToDelete?.let { task ->
            DeleteConfirmDialog(
                task = task,
                onConfirm = viewModel::confirmDelete,
                onDismiss = viewModel::cancelDelete
            )
        }
    }
}

/**
 * Сетка матрицы — 4 квадранта в компоновке 2×2.
 * Разделители между квадрантами — центральный крест.
 */
@Composable
private fun MatrixGrid(
    uiState: MatrixUiState,
    onAddTask: (Quadrant) -> Unit,
    onDragStart: (com.eisenhower.matrix.data.model.Task) -> Unit,
    onDragCancel: () -> Unit,
    onDrop: (Quadrant) -> Unit,
    onHover: (Quadrant?) -> Unit,
    onDeleteRequest: (com.eisenhower.matrix.data.model.Task) -> Unit,
    onEditRequest: (com.eisenhower.matrix.data.model.Task) -> Unit
) {
    // Толщина разделителя между квадрантами
    val dividerThickness = 3.dp
    val dividerColor = DividerColor

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Метки осей (СРОЧНО / НЕ СРОЧНО)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AxisLabel("⚡ СРОЧНО", Modifier.weight(1f))
            Spacer(Modifier.width(dividerThickness))
            AxisLabel("💤 НЕ СРОЧНО", Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxSize()) {
            // Метка оси Y (ВАЖНО / НЕ ВАЖНО) — вертикально
            VerticalAxisLabel()

            // Квадранты
            Column(modifier = Modifier.weight(1f)) {

                // Верхняя строка: Q1 и Q2
                Row(modifier = Modifier.weight(1f)) {
                    QuadrantCell(
                        quadrant = Quadrant.DO_FIRST,
                        modifier = Modifier.weight(1f),
                        uiState = uiState,
                        onAddTask = onAddTask,
                        onDragStart = onDragStart,
                        onDrop = onDrop,
                        onHover = onHover,
                        onDeleteRequest = onDeleteRequest,
                        onEditRequest = onEditRequest
                    )
                    Spacer(
                        modifier = Modifier
                            .width(dividerThickness)
                            .fillMaxHeight()
                            .background(dividerColor)
                    )
                    QuadrantCell(
                        quadrant = Quadrant.SCHEDULE,
                        modifier = Modifier.weight(1f),
                        uiState = uiState,
                        onAddTask = onAddTask,
                        onDragStart = onDragStart,
                        onDrop = onDrop,
                        onHover = onHover,
                        onDeleteRequest = onDeleteRequest,
                        onEditRequest = onEditRequest
                    )
                }

                // Горизонтальный разделитель
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dividerThickness)
                        .background(dividerColor)
                )

                // Нижняя строка: Q3 и Q4
                Row(modifier = Modifier.weight(1f)) {
                    QuadrantCell(
                        quadrant = Quadrant.DELEGATE,
                        modifier = Modifier.weight(1f),
                        uiState = uiState,
                        onAddTask = onAddTask,
                        onDragStart = onDragStart,
                        onDrop = onDrop,
                        onHover = onHover,
                        onDeleteRequest = onDeleteRequest,
                        onEditRequest = onEditRequest
                    )
                    Spacer(
                        modifier = Modifier
                            .width(dividerThickness)
                            .fillMaxHeight()
                            .background(dividerColor)
                    )
                    QuadrantCell(
                        quadrant = Quadrant.ELIMINATE,
                        modifier = Modifier.weight(1f),
                        uiState = uiState,
                        onAddTask = onAddTask,
                        onDragStart = onDragStart,
                        onDrop = onDrop,
                        onHover = onHover,
                        onDeleteRequest = onDeleteRequest,
                        onEditRequest = onEditRequest
                    )
                }
            }
        }
    }
}

/**
 * Одна ячейка сетки — оборачивает QuadrantPanel.
 */
@Composable
private fun QuadrantCell(
    quadrant: Quadrant,
    modifier: Modifier,
    uiState: MatrixUiState,
    onAddTask: (Quadrant) -> Unit,
    onDragStart: (com.eisenhower.matrix.data.model.Task) -> Unit,
    onDrop: (Quadrant) -> Unit,
    onHover: (Quadrant?) -> Unit,
    onDeleteRequest: (com.eisenhower.matrix.data.model.Task) -> Unit,
    onEditRequest: (com.eisenhower.matrix.data.model.Task) -> Unit
) {
    QuadrantPanel(
        modifier = modifier.fillMaxSize(),
        quadrant = quadrant,
        tasks = uiState.tasksByQuadrant[quadrant] ?: emptyList(),
        isHovered = uiState.isDragging && uiState.hoveredQuadrant == quadrant,
        isDragging = uiState.isDragging,
        onAddTask = { onAddTask(quadrant) },
        onDragStart = onDragStart,
        onDropHere = { onDrop(quadrant) },
        onDropEnter = { onHover(quadrant) },
        onDropExit = { onHover(null) },
        onDeleteRequest = onDeleteRequest,
        onEditRequest = onEditRequest,
        draggingTask = uiState.draggingTask
    )
}

/**
 * Метка оси X (срочность).
 */
@Composable
private fun AxisLabel(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Вертикальная метка оси Y (важность).
 */
@Composable
private fun VerticalAxisLabel() {
    Column(
        modifier = Modifier
            .width(16.dp)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Rotate-текст через Box с rotate modifier
        androidx.compose.ui.graphics.graphicsLayer { }
        Text(
            text = "В",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "А",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Ж",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Н",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "О",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * TopBar с переключателем темы.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MatrixTopBar(
    themePreference: ThemePreference,
    onThemeChange: (ThemePreference) -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Матрица Эйзенхауэра",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Управление приоритетами",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {
            // Переключатель темы (три состояния)
            IconButton(
                onClick = {
                    onThemeChange(
                        when (themePreference) {
                            ThemePreference.SYSTEM -> ThemePreference.LIGHT
                            ThemePreference.LIGHT -> ThemePreference.DARK
                            ThemePreference.DARK -> ThemePreference.SYSTEM
                        }
                    )
                }
            ) {
                Icon(
                    imageVector = when (themePreference) {
                        ThemePreference.DARK -> Icons.Default.DarkMode
                        ThemePreference.LIGHT -> Icons.Default.LightMode
                        ThemePreference.SYSTEM -> Icons.Default.SettingsBrightness
                    },
                    contentDescription = "Сменить тему"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}
