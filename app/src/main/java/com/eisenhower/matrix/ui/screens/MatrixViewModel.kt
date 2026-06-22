package com.eisenhower.matrix.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eisenhower.matrix.data.model.Quadrant
import com.eisenhower.matrix.data.model.Task
import com.eisenhower.matrix.data.repository.TaskRepository
import com.eisenhower.matrix.utils.PreferencesManager
import com.eisenhower.matrix.utils.ThemePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MatrixUiState(
    val tasksByQuadrant: Map<Quadrant, List<Task>> = emptyMap(),
    val isDragging: Boolean = false,
    val draggingTask: Task? = null,
    val hoveredQuadrant: Quadrant? = null,
    val showTutorial: Boolean = false,
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val taskToDelete: Task? = null,
    val taskToEdit: Task? = null,
    val showAddDialog: Boolean = false,
    val addToQuadrant: Quadrant? = null
)

@HiltViewModel
class MatrixViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatrixUiState())
    val uiState: StateFlow<MatrixUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getTasksByQuadrant(Quadrant.DO_FIRST),
                repository.getTasksByQuadrant(Quadrant.SCHEDULE),
                repository.getTasksByQuadrant(Quadrant.DELEGATE),
                repository.getTasksByQuadrant(Quadrant.ELIMINATE)
            ) { q1, q2, q3, q4 ->
                mapOf(Quadrant.DO_FIRST to q1, Quadrant.SCHEDULE to q2,
                      Quadrant.DELEGATE to q3, Quadrant.ELIMINATE to q4)
            }.collect { map -> _uiState.update { it.copy(tasksByQuadrant = map) } }
        }
        viewModelScope.launch {
            preferencesManager.isTutorialShown.collect { shown ->
                _uiState.update { it.copy(showTutorial = !shown) }
            }
        }
        viewModelScope.launch {
            preferencesManager.themePreference.collect { theme ->
                _uiState.update { it.copy(themePreference = theme) }
            }
        }
    }

    // Drag & Drop
    fun onDragStart(task: Task) = _uiState.update { it.copy(isDragging = true, draggingTask = task) }
    fun onDragHover(quadrant: Quadrant?) = _uiState.update { it.copy(hoveredQuadrant = quadrant) }
    fun onDrop(targetQuadrant: Quadrant) {
        val task = _uiState.value.draggingTask ?: return
        if (task.quadrant != targetQuadrant) {
            viewModelScope.launch { repository.moveTaskToQuadrant(task, targetQuadrant) }
        }
        _uiState.update { it.copy(isDragging = false, draggingTask = null, hoveredQuadrant = null) }
    }
    fun onDragCancel() = _uiState.update { it.copy(isDragging = false, draggingTask = null, hoveredQuadrant = null) }

    // Добавление
    fun showAddDialog(quadrant: Quadrant) = _uiState.update { it.copy(showAddDialog = true, addToQuadrant = quadrant) }
    fun dismissAddDialog() = _uiState.update { it.copy(showAddDialog = false, addToQuadrant = null) }
    fun addTask(title: String, description: String, quadrant: Quadrant, dueDate: Long?) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.createTask(Task(title = title.trim(), description = description.trim(), quadrant = quadrant, dueDate = dueDate))
        }
        dismissAddDialog()
    }

    // Редактирование
    fun showEditDialog(task: Task) = _uiState.update { it.copy(taskToEdit = task) }
    fun dismissEditDialog() = _uiState.update { it.copy(taskToEdit = null) }
    fun updateTask(task: Task, newTitle: String, newDescription: String, newDueDate: Long?) {
        viewModelScope.launch {
            repository.updateTask(task.copy(title = newTitle.trim(), description = newDescription.trim(), dueDate = newDueDate))
        }
        dismissEditDialog()
    }

    // Удаление
    fun requestDelete(task: Task) = _uiState.update { it.copy(taskToDelete = task) }
    fun confirmDelete() {
        val task = _uiState.value.taskToDelete ?: return
        viewModelScope.launch { repository.deleteTask(task) }
        _uiState.update { it.copy(taskToDelete = null) }
    }
    fun cancelDelete() = _uiState.update { it.copy(taskToDelete = null) }

    // Настройки
    fun dismissTutorial() = viewModelScope.launch { preferencesManager.setTutorialShown() }
    fun setTheme(theme: ThemePreference) = viewModelScope.launch { preferencesManager.setThemePreference(theme) }
}