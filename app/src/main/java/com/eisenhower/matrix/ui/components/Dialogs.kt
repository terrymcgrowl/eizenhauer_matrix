package com.eisenhower.matrix.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.eisenhower.matrix.data.model.Quadrant
import com.eisenhower.matrix.data.model.Task
import com.eisenhower.matrix.ui.theme.colors
import com.eisenhower.matrix.ui.theme.icon
import kotlinx.coroutines.delay

// ============================================================
// Диалог добавления задачи
// ============================================================

/**
 * Диалог создания новой задачи.
 * @param quadrant В какой квадрант добавляем
 * @param onConfirm Подтверждение с введёнными данными
 * @param onDismiss Закрытие без сохранения
 */
@Composable
fun AddTaskDialog(
    quadrant: Quadrant,
    onConfirm: (title: String, description: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val quadrantColors = quadrant.colors()

    // Автофокус на поле названия
    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                // Заголовок диалога с цветовой маркировкой квадранта
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = quadrant.icon(),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Новая задача",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = quadrant.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = quadrantColors.accent
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Поле названия
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название задачи") },
                    placeholder = { Text("Введите название...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = quadrantColors.accent,
                        focusedLabelColor = quadrantColors.accent
                    )
                )

                Spacer(Modifier.height(8.dp))

                // Поле описания (опционально)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание (необязательно)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = quadrantColors.accent,
                        focusedLabelColor = quadrantColors.accent
                    )
                )

                Spacer(Modifier.height(20.dp))

                // Кнопки действий
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Отмена")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(title, description) },
                        enabled = title.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = quadrantColors.accent
                        )
                    ) {
                        Text("Добавить", color = Color.White)
                    }
                }
            }
        }
    }
}

// ============================================================
// Диалог редактирования задачи
// ============================================================

/**
 * Диалог редактирования существующей задачи (открывается долгим нажатием).
 */
@Composable
fun EditTaskDialog(
    task: Task,
    onConfirm: (title: String, description: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    val quadrantColors = task.quadrant.colors()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    text = "Редактировать задачу",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = quadrantColors.accent,
                        focusedLabelColor = quadrantColors.accent
                    )
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = quadrantColors.accent,
                        focusedLabelColor = quadrantColors.accent
                    )
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Отмена") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(title, description) },
                        enabled = title.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = quadrantColors.accent
                        )
                    ) {
                        Text("Сохранить", color = Color.White)
                    }
                }
            }
        }
    }
}

// ============================================================
// Диалог подтверждения удаления
// ============================================================

/**
 * Диалог подтверждения удаления задачи.
 * Появляется после свайпа карточки.
 */
@Composable
fun DeleteConfirmDialog(
    task: Task,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text("🗑️", style = MaterialTheme.typography.headlineSmall)
        },
        title = {
            Text(
                text = "Удалить задачу?",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = "«${task.title}» будет удалена навсегда.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Удалить", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

// ============================================================
// Туториал (первый запуск)
// ============================================================

/**
 * Оверлей туториала для первого запуска.
 * Объясняет drag & drop перемещение задач между квадрантами.
 */
@Composable
fun TutorialOverlay(
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Анимированная иконка перетаскивания
                Icon(
                    imageVector = Icons.Default.DragIndicator,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Матрица Эйзенхауэра",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                // Объяснение квадрантов
                QuadrantLegendItem("🔥", "Делать сейчас", "Срочно + Важно")
                QuadrantLegendItem("📅", "Запланировать", "Важно, не срочно")
                QuadrantLegendItem("🤝", "Делегировать", "Срочно, не важно")
                QuadrantLegendItem("🗑️", "Устранить", "Не срочно, не важно")

                Spacer(Modifier.height(20.dp))

                // Ключевая подсказка
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("👆 Удерживай + тяни", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Перетащи задачу в другой квадрант, чтобы изменить её приоритет",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "← Свайп для удаления   •   Долгое нажатие для редактирования",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Понятно, начать!")
                }
            }
        }
    }
}

@Composable
private fun QuadrantLegendItem(icon: String, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.width(10.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
