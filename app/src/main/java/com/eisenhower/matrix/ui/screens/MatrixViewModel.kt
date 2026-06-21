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

/**
 * Состояние UI главного экрана матрицы.
 */
data class MatrixUiState(
    // Задачи по квадрантам (загружены из БД)
    val tasksByQuadrant: Map<Quadrant, List<Task>> = emptyMap(),
    // Идёт ли перетаскивание в данный момент
    val isDragging: Boolean = false,
    // Задача, которую сейчас тащат
    val draggingTask: Task? = null,
    // Квадрант, над которым находится задача при перетаскивании
    val hoveredQuadrant: Quadrant? = null,
    // Показывать ли туториал
    val showTutorial: Boolean = false,
    // Текущая тема
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    // Задача для удаления (ожидает подтверждения)
    val taskToDelete: Task? = null,
    // Задача для редактирования
    val taskToEdit: Task? = null,
    // Показывать ли диалог добавления задачи
    val showAddDialog: Boolean = false,
    // Квадрант для новой задачи
    val addToQuadrant: Quadrant? = null
)

/**
 * ViewModel главного экрана.
 * Собирает данные из всех 4 квадрантов и предоставляет действия пользователя.
 *
 * @HiltViewModel — Hilt управляет созданием ViewModel.
 */
@HiltViewModel
class MatrixViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatrixUiState())
    val uiState: StateFlow<MatrixUiState> = _uiState.asStateFlow()

    init {
        // Загружаем задачи для каждого квадранта в один Map
        viewModelScope.launch {
            // Объединяем 4 потока из БД в единое состояние UI
            combine(
                repository.getTasksByQuadrant(Quadrant.DO_FIRST),
                repository.getTasksByQuadrant(Quadrant.SCHEDULE),
                repository.getTasksByQuadrant(Quadrant.DELEGATE),
                repository.getTasksByQuadrant(Quadrant.ELIMINATE)
            ) { doFirst, schedule, delegate, eliminate ->
                mapOf(
                    Quadrant.DO_FIRST to doFirst,
                    Quadrant.SCHEDULE to schedule,
                    Quadrant.DELEGATE to delegate,
                    Quadrant.ELIMINATE to eliminate
                )
            }.collect { taskMap ->
                _uiState.update { it.copy(tasksByQuadrant = taskMap) }
            }
        }

        // Следим за настройками (туториал, тема)
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

    // ========================
    // Drag & Drop
    // ========================

    /**
     * Начало перетаскивания задачи.
     */
    fun onDragStart(task: Task) {
        _uiState.update {
            it.copy(isDragging = true, draggingTask = task)
        }
    }

    /**
     * Обновление: курсор над квадрантом.
     */
    fun onDragHover(quadrant: Quadrant?) {
        _uiState.update { it.copy(hoveredQuadrant = quadrant) }
    }

    /**
     * Завершение перетаскивания: перемещаем задачу в новый квадрант.
     */
    fun onDrop(targetQuadrant: Quadrant) {
        val task = _uiState.value.draggingTask ?: return
        if (task.quadrant != targetQuadrant) {
            viewModelScope.launch {
                repository.moveTaskToQuadrant(task, targetQuadrant)
            }
        }
        _uiState.update {
            it.copy(isDragging = false, draggingTask = null, hoveredQuadrant = null)
        }
    }

    /**
     * Отмена перетаскивания (палец убран за пределы экрана).
     */
    fun onDragCancel() {
        _uiState.update {
            it.copy(isDragging = false, draggingTask = null, hoveredQuadrant = null)
        }
    }

    // ========================
    // Добавление задачи
    // ========================

    fun showAddDialog(quadrant: Quadrant) {
        _uiState.update { it.copy(showAddDialog = true, addToQuadrant = quadrant) }
    }

    fun dismissAddDialog() {
        _uiState.update { it.copy(showAddDialog = false, addToQuadrant = null) }
    }

    fun addTask(title: String, description: String, quadrant: Quadrant) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.createTask(
                Task(title = title.trim(), description = description.trim(), quadrant = quadrant)
            )
        }
        dismissAddDialog()
    }

    // ========================
    // Редактирование
    // ========================

    fun showEditDialog(task: Task) {
        _uiState.update { it.copy(taskToEdit = task) }
    }

    fun dismissEditDialog() {
        _uiState.update { it.copy(taskToEdit = null) }
    }

    fun updateTask(task: Task, newTitle: String, newDescription: String) {
        viewModelScope.launch {
            repository.updateTask(
                task.copy(title = newTitle.trim(), description = newDescription.trim())
            )
        }
        dismissEditDialog()
    }

    // ========================
    // Удаление
    // ========================

    fun requestDelete(task: Task) {
        _uiState.update { it.copy(taskToDelete = task) }
    }

    fun confirmDelete() {
        val task = _uiState.value.taskToDelete ?: return
        viewModelScope.launch { repository.deleteTask(task) }
        _uiState.update { it.copy(taskToDelete = null) }
    }

    fun cancelDelete() {
        _uiState.update { it.copy(taskToDelete = null) }
    }

    // ========================
    // Туториал и тема
    // ========================

    fun dismissTutorial() {
        viewModelScope.launch { preferencesManager.setTutorialShown() }
    }

    fun setTheme(theme: ThemePreference) {
        viewModelScope.launch { preferencesManager.setThemePreference(theme) }
    }
}
