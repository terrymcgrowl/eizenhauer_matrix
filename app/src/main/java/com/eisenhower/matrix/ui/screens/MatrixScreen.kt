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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatrixScreen(
    viewModel: MatrixViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
                MatrixGrid(
                    uiState = uiState,
                    onAddTask = viewModel::showAddDialog,
                    onDragStart = viewModel::onDragStart,
                    onDrop = viewModel::onDrop,
                    onHover = viewModel::onDragHover,
                    onDeleteRequest = viewModel::requestDelete,
                    onEditRequest = viewModel::showEditDialog
                )

                if (uiState.showTutorial) {
                    TutorialOverlay(onDismiss = viewModel::dismissTutorial)
                }
            }
        }

        if (uiState.showAddDialog && uiState.addToQuadrant != null) {
            AddTaskDialog(
                quadrant = uiState.addToQuadrant!!,
                onConfirm = { title, desc ->
                    viewModel.addTask(title, desc, uiState.addToQuadrant!!)
                },
                onDismiss = viewModel::dismissAddDialog
            )
        }

        uiState.taskToEdit?.let { task ->
            EditTaskDialog(
                task = task,
                onConfirm = { title, desc -> viewModel.updateTask(task, title, desc) },
                onDismiss = viewModel::dismissEditDialog
            )
        }

        uiState.taskToDelete?.let { task ->
            DeleteConfirmDialog(
                task = task,
                onConfirm = viewModel::confirmDelete,
                onDismiss = viewModel::cancelDelete
            )
        }
    }
}

@Composable
private fun MatrixGrid(
    uiState: MatrixUiState,
    onAddTask: (Quadrant) -> Unit,
    onDragStart: (com.eisenhower.matrix.data.model.Task) -> Unit,
    onDrop: (Quadrant) -> Unit,
    onHover: (Quadrant?) -> Unit,
    onDeleteRequest: (com.eisenhower.matrix.data.model.Task) -> Unit,
    onEditRequest: (com.eisenhower.matrix.data.model.Task) -> Unit
) {
    val dividerThickness = 3.dp
    val dividerColor = DividerColor

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Метки оси X
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
            // Метка оси Y
            VerticalAxisLabel()

            Column(modifier = Modifier.weight(1f)) {
                // Верхняя строка
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

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dividerThickness)
                        .background(dividerColor)
                )

                // Нижняя строка
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

@Composable
private fun VerticalAxisLabel() {
    Column(
        modifier = Modifier
            .width(16.dp)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf("В", "А", "Ж", "Н", "О").forEach { letter ->
            Text(
                text = letter,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

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