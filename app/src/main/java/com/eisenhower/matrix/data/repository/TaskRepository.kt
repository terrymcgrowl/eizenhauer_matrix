package com.eisenhower.matrix.data.repository

import com.eisenhower.matrix.data.db.TaskDao
import com.eisenhower.matrix.data.model.Quadrant
import com.eisenhower.matrix.data.model.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(private val taskDao: TaskDao) {

    fun getTasksByQuadrant(quadrant: Quadrant): Flow<List<Task>> =
        taskDao.getTasksByQuadrant(quadrant.name)

    suspend fun createTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) =
        taskDao.updateTask(task.copy(updatedAt = System.currentTimeMillis()))

    suspend fun moveTaskToQuadrant(task: Task, newQuadrant: Quadrant) =
        taskDao.updateTask(task.copy(quadrant = newQuadrant, updatedAt = System.currentTimeMillis()))

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
}