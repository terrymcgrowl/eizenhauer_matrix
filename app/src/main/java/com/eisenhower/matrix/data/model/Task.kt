package com.eisenhower.matrix.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Quadrant(
    val title: String,
    val subtitle: String,
    val isUrgent: Boolean,
    val isImportant: Boolean
) {
    DO_FIRST("Делать сейчас", "Срочно · Важно", true, true),
    SCHEDULE("Запланировать", "Важно · Не срочно", false, true),
    DELEGATE("Делегировать", "Срочно · Не важно", true, false),
    ELIMINATE("Устранить", "Не срочно · Не важно", false, false);
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val quadrant: Quadrant,
    // Срок выполнения в мс от эпохи; null = не задан
    val dueDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)