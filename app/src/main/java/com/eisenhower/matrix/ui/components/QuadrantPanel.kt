package com.eisenhower.matrix.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.eisenhower.matrix.data.model.Quadrant
import com.eisenhower.matrix.data.model.Task
import com.eisenhower.matrix.ui.theme.colors

@Composable
fun QuadrantPanel(
    quadrant: Quadrant,
    tasks: List<Task>,
    isHovered: Boolean,
    isDragging: Boolean,
    onAddTask: () -> Unit,
    onDragStart: (Task) -> Unit,
    onDropHere: () -> Unit,
    onDropEnter: () -> Unit,
    onDropExit: () -> Unit,
    onDeleteRequest: (Task) -> Unit,
    onEditRequest: (Task) -> Unit,
    draggingTask: Task?,
    modifier: Modifier = Modifier
) {
    val accent = quadrant.colors().accent

    val borderWidth by animateDpAsState(
        targetValue = if (isHovered) 1.5.dp else 0.dp,
        animationSpec = spring(),
        label = "border"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(0.dp))
            .background(MaterialTheme.colorScheme.background)
            .then(
                if (isHovered) Modifier.border(borderWidth, accent.copy(alpha = 0.5f))
                else Modifier
            )
            .dropTarget(
                onDragEnter = onDropEnter,
                onDragExit = onDropExit,
                onDrop = onDropHere
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Заголовок — минималистичный
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 4.dp, top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Цветная точка вместо иконки
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(accent)
                )
                Spacer(Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = quadrant.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = quadrant.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // Счётчик задач
                if (tasks.isNotEmpty()) {
                    Text(
                        text = tasks.size.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(2.dp))
                }
                IconButton(onClick = onAddTask, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Добавить",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Список задач
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    if (task.id != draggingTask?.id) {
                        TaskCard(
                            task = task,
                            isDragging = false,
                            onLongPress = { onEditRequest(task) },
                            onSwipeToDelete = { onDeleteRequest(task) },
                            dragHandleModifier = Modifier.draggable(
                                onDragStart = { onDragStart(task) },
                                onDragEnd = {},
                                onDragCancel = {},
                                dragContent = {
                                    TaskCard(task = task, isDragging = true, onLongPress = {}, onSwipeToDelete = {})
                                }
                            )
                        )
                    }
                }
                if (tasks.isEmpty() && !isDragging) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "пусто",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }
        }
    }
}