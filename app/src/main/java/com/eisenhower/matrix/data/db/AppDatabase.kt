package com.eisenhower.matrix.data.db

import androidx.room.*
import com.eisenhower.matrix.data.model.Quadrant
import com.eisenhower.matrix.data.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Конвертер для хранения enum Quadrant как строки в SQLite.
 * Room не умеет хранить enum напрямую — используем имя константы.
 */
class Converters {
    @TypeConverter
    fun fromQuadrant(quadrant: Quadrant): String = quadrant.name

    @TypeConverter
    fun toQuadrant(value: String): Quadrant = Quadrant.valueOf(value)
}

/**
 * DAO (Data Access Object) — интерфейс для работы с таблицей задач.
 * Room автоматически генерирует реализацию в compile-time.
 */
@Dao
interface TaskDao {

    /**
     * Получить все задачи конкретного квадранта как реактивный поток.
     * Flow автоматически обновляется при изменении данных в БД.
     */
    @Query("SELECT * FROM tasks WHERE quadrant = :quadrant AND isCompleted = 0 ORDER BY createdAt DESC")
    fun getTasksByQuadrant(quadrant: String): Flow<List<Task>>

    /**
     * Получить все задачи (для экспорта / отладки).
     */
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>

    /**
     * Вставить новую задачу. Возвращает id вставленной записи.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    /**
     * Обновить существующую задачу (редактирование / drag&drop).
     */
    @Update
    suspend fun updateTask(task: Task)

    /**
     * Удалить задачу по объекту.
     */
    @Delete
    suspend fun deleteTask(task: Task)

    /**
     * Получить задачу по ID (для редактирования).
     */
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?
}

/**
 * База данных Room.
 * @Database — аннотация, описывающая схему: список сущностей и версию БД.
 * При изменении модели данных нужно увеличивать version и добавлять Migration.
 */
@Database(
    entities = [Task::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
