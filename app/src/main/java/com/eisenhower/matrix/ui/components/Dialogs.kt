package com.eisenhower.matrix.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.eisenhower.matrix.data.model.Quadrant
import com.eisenhower.matrix.data.model.Task
import com.eisenhower.matrix.ui.theme.colors
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

private val displayFormatter = SimpleDateFormat("d MMMM yyyy", Locale("ru"))

// ── Пикер даты через системный DatePickerDialog ──────────────────────────────

@Composable
fun rememberDatePickerState(initialMs: Long?): Pair<Long?, () -> Unit> {
    val context = LocalContext.current
    var selectedMs by remember { mutableStateOf(initialMs) }

    val showPicker = {
        val cal = Calendar.getInstance().apply {
            if (initialMs != null) timeInMillis = initialMs
        }
        DatePickerDialog(
            context,
            { _, year, month, day ->
                selectedMs = Calendar.getInstance()
                    .apply { set(year, month, day, 23, 59, 59) }
                    .timeInMillis
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    return selectedMs to showPicker
}

// ── Диалог добавления задачи ──────────────────────────────────────────────────

@Composable
fun AddTaskDialog(
    quadrant: Quadrant,
    onConfirm: (title: String, description: String, dueDate: Long?) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val (dueDate, showDatePicker) = rememberDatePickerState(null)
    val focusRequester = remember { FocusRequester() }
    val accent = quadrant.colors().accent

    LaunchedEffect(Unit) { delay(100); focusRequester.requestFocus() }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = quadrant.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = accent
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Название задачи") },
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accent,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Описание (необязательно)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accent,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(Modifier.height(8.dp))

                // Кнопка выбора срока
                DueDateButton(dueDate = dueDate, onPickDate = showDatePicker)

                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Отмена") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(title, description, dueDate) },
                        enabled = title.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = accent)
                    ) { Text("Добавить") }
                }
            }
        }
    }
}

// ── Диалог редактирования ─────────────────────────────────────────────────────

@Composable
fun EditTaskDialog(
    task: Task,
    onConfirm: (title: String, description: String, dueDate: Long?) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    val (dueDate, showDatePicker) = rememberDatePickerState(task.dueDate)
    val accent = task.quadrant.colors().accent

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Редактировать", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accent,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accent,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(Modifier.height(8.dp))
                DueDateButton(dueDate = dueDate, onPickDate = showDatePicker)

                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Отмена") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(title, description, dueDate) },
                        enabled = title.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = accent)
                    ) { Text("Сохранить") }
                }
            }
        }
    }
}

// ── Кнопка выбора срока ───────────────────────────────────────────────────────

@Composable
private fun DueDateButton(dueDate: Long?, onPickDate: () -> Unit) {
    TextButton(
        onClick = onPickDate,
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 4.dp)
    ) {
        Icon(
            Icons.Default.CalendarToday,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = if (dueDate != null) displayFormatter.format(Date(dueDate))
                   else "Добавить срок",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ── Диалог удаления ───────────────────────────────────────────────────────────

@Composable
fun DeleteConfirmDialog(task: Task, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Удалить задачу?", style = MaterialTheme.typography.titleMedium) },
        text = {
            Text(
                "«${task.title}» будет удалена.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Удалить", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

// ── Туториал ──────────────────────────────────────────────────────────────────

@Composable
fun TutorialOverlay(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.padding(32.dp).fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Матрица Эйзенхауэра",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(16.dp))
                listOf(
                    "Удерживай карточку и перетащи в другой квадрант — приоритет изменится",
                    "Свайп влево или вправо — удалить задачу",
                    "Долгое нажатие — редактировать"
                ).forEach { hint ->
                    Text(
                        "· $hint",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                    )
                }
                Spacer(Modifier.height(20.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Понятно")
                }
            }
        }
    }
}