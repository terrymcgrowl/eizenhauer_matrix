package com.eisenhower.matrix.data.db

import androidx.room.*
import com.eisenhower.matrix.data.model.Quadrant
import com.eisenhower.matrix.data.model.Task
import kotlinx.coroutines.flow.Flow

class Converters {
    @TypeConverter
    fun fromQuadrant(quadrant: Quadrant): String = quadrant.name
    @TypeConverter
    fun toQuadrant(value: String): Quadrant = Quadrant.valueOf(value)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE quadrant = :quadrant ORDER BY CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END, dueDate ASC, createdAt DESC")
    fun getTasksByQuadrant(quadrant: String): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?
}

// Версия 2 — добавлено поле dueDate (миграция через fallbackToDestructiveMigration для dev)
@Database(entities = [Task::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}