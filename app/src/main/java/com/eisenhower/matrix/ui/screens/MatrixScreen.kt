package com.eisenhower.matrix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eisenhower.matrix.data.model.Quadrant
import com.eisenhower.matrix.ui.components.*
import com.eisenhower.matrix.ui.theme.DividerColor
import com.eisenhower.matrix.ui.theme.DividerColorDark
import com.eisenhower.matrix.utils.ThemePreference
import androidx.compose.foundation.isSystemInDarkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatrixScreen(viewModel: MatrixViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    DragDropContainer(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Эйзенхауэр", style = MaterialTheme.typography.titleMedium) },
                    actions = {
                        IconButton(onClick = {
                            viewModel.setTheme(when (uiState.themePreference) {
                                ThemePreference.SYSTEM -> ThemePreference.LIGHT
                                ThemePreference.LIGHT  -> ThemePreference.DARK
                                ThemePreference.DARK   -> ThemePreference.SYSTEM
                            })
                        }) {
                            Icon(
                                imageVector = when (uiState.themePreference) {
                                    ThemePreference.DARK   -> Icons.Default.DarkMode
                                    ThemePreference.LIGHT  -> Icons.Default.LightMode
                                    ThemePreference.SYSTEM -> Icons.Default.SettingsBrightness
                                },
                                contentDescription = "Тема",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                MatrixGrid(uiState = uiState, viewModel = viewModel)
                if (uiState.showTutorial) {
                    TutorialOverlay(onDismiss = viewModel::dismissTutorial)
                }
            }
        }

        // Диалоги
        if (uiState.showAddDialog && uiState.addToQuadrant != null) {
            AddTaskDialog(
                quadrant = uiState.addToQuadrant!!,
                onConfirm = { title, desc, due -> viewModel.addTask(title, desc, uiState.addToQuadrant!!, due) },
                onDismiss = viewModel::dismissAddDialog
            )
        }
        uiState.taskToEdit?.let { task ->
            EditTaskDialog(
                task = task,
                onConfirm = { title, desc, due -> viewModel.updateTask(task, title, desc, due) },
                onDismiss = viewModel::dismissEditDialog
            )
        }
        uiState.taskToDelete?.let { task ->
            DeleteConfirmDialog(task = task, onConfirm = viewModel::confirmDelete, onDismiss = viewModel::cancelDelete)
        }
    }
}

@Composable
private fun MatrixGrid(uiState: MatrixUiState, viewModel: MatrixViewModel) {
    val isDark = isSystemInDarkTheme()
    val divider = if (isDark) DividerColorDark else DividerColor
    val dividerSize = 1.dp

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.weight(1f)) {
            QuadrantCell(Quadrant.DO_FIRST, Modifier.weight(1f), uiState, viewModel)
            Spacer(Modifier.width(dividerSize).fillMaxHeight().background(divider))
            QuadrantCell(Quadrant.SCHEDULE, Modifier.weight(1f), uiState, viewModel)
        }
        Spacer(Modifier.height(dividerSize).fillMaxWidth().background(divider))
        Row(modifier = Modifier.weight(1f)) {
            QuadrantCell(Quadrant.DELEGATE, Modifier.weight(1f), uiState, viewModel)
            Spacer(Modifier.width(dividerSize).fillMaxHeight().background(divider))
            QuadrantCell(Quadrant.ELIMINATE, Modifier.weight(1f), uiState, viewModel)
        }
    }
}

@Composable
private fun QuadrantCell(
    quadrant: Quadrant,
    modifier: Modifier,
    uiState: MatrixUiState,
    viewModel: MatrixViewModel
) {
    QuadrantPanel(
        modifier = modifier.fillMaxSize(),
        quadrant = quadrant,
        tasks = uiState.tasksByQuadrant[quadrant] ?: emptyList(),
        isHovered = uiState.isDragging && uiState.hoveredQuadrant == quadrant,
        isDragging = uiState.isDragging,
        onAddTask = { viewModel.showAddDialog(quadrant) },
        onDragStart = viewModel::onDragStart,
        onDropHere = { viewModel.onDrop(quadrant) },
        onDropEnter = { viewModel.onDragHover(quadrant) },
        onDropExit = { viewModel.onDragHover(null) },
        onDeleteRequest = viewModel::requestDelete,
        onEditRequest = viewModel::showEditDialog,
        draggingTask = uiState.draggingTask
    )
}