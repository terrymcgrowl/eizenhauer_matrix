package com.eisenhower.matrix.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Квадрант матрицы Эйзенхауэра.
 * Каждый квадрант определяется сочетанием срочности и важности.
 */
enum class Quadrant(
    val title: String,
    val subtitle: String,
    val isUrgent: Boolean,
    val isImportant: Boolean
) {
    // Q1: Срочно + Важно → делать немедленно
    DO_FIRST(
        title = "Делать сейчас",
        subtitle = "Срочно и важно",
        isUrgent = true,
        isImportant = true
    ),
    // Q2: Не срочно + Важно → планировать
    SCHEDULE(
        title = "Запланировать",
        subtitle = "Важно, не срочно",
        isUrgent = false,
        isImportant = true
    ),
    // Q3: Срочно + Не важно → делегировать
    DELEGATE(
        title = "Делегировать",
        subtitle = "Срочно, не важно",
        isUrgent = true,
        isImportant = false
    ),
    // Q4: Не срочно + Не важно → устранить
    ELIMINATE(
        title = "Устранить",
        subtitle = "Не срочно, не важно",
        isUrgent = false,
        isImportant = false
    );

    companion object {
        /**
         * Определяет квадрант по координатам сетки (строка/столбец).
         * Сетка 2x2: строка 0 = верх, столбец 0 = лево
         */
        fun fromGridPosition(row: Int, col: Int): Quadrant {
            return when {
                row == 0 && col == 0 -> DO_FIRST
                row == 0 && col == 1 -> SCHEDULE
                row == 1 && col == 0 -> DELEGATE
                else -> ELIMINATE
            }
        }
    }
}

/**
 * Сущность задачи в базе данных Room.
 * @param id Уникальный идентификатор (автогенерация)
 * @param title Название задачи
 * @param description Подробное описание (опционально)
 * @param quadrant Текущий квадрант задачи
 * @param createdAt Время создания (мс от эпохи)
 * @param updatedAt Время последнего изменения (мс от эпохи)
 * @param isCompleted Отметка о выполнении
 */
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val quadrant: Quadrant,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false
)
