package com.eisenhower.matrix.data.repository

import com.eisenhower.matrix.data.db.TaskDao
import com.eisenhower.matrix.data.model.Quadrant
import com.eisenhower.matrix.data.model.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторий задач — единая точка доступа к данным.
 * Паттерн Repository изолирует UI от деталей хранения данных.
 * ViewModel работает только с репозиторием, не зная о Room напрямую.
 *
 * @Singleton — один экземпляр на всё приложение (Hilt).
 */
@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {

    /**
     * Получить задачи квадранта как поток (реактивно обновляется).
     */
    fun getTasksByQuadrant(quadrant: Quadrant): Flow<List<Task>> {
        return taskDao.getTasksByQuadrant(quadrant.name)
    }

    /**
     * Все задачи (поток).
     */
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    /**
     * Создать задачу. Возвращает присвоенный id.
     */
    suspend fun createTask(task: Task): Long = taskDao.insertTask(task)

    /**
     * Обновить задачу с новым временем изменения.
     */
    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.copy(updatedAt = System.currentTimeMillis()))
    }

    /**
     * Переместить задачу в другой квадрант (drag & drop результат).
     */
    suspend fun moveTaskToQuadrant(task: Task, newQuadrant: Quadrant) {
        taskDao.updateTask(
            task.copy(
                quadrant = newQuadrant,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    /**
     * Удалить задачу (после подтверждения свайпа).
     */
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    /**
     * Получить задачу по id для редактирования.
     */
    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)
}
