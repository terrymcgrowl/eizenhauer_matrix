package com.eisenhower.matrix.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eisenhower.matrix.data.model.Quadrant
import com.eisenhower.matrix.data.model.Task
import com.eisenhower.matrix.ui.theme.colors
import com.eisenhower.matrix.ui.theme.icon

/**
 * Один квадрант матрицы Эйзенхауэра.
 * Содержит заголовок, список задач и кнопку добавления.
 * Является одновременно drop-target для drag & drop.
 *
 * @param quadrant Тип квадранта
 * @param tasks Список задач в этом квадранте
 * @param isHovered Подсветка, когда над ним тащат карточку
 * @param isDragging Идёт ли перетаскивание вообще
 * @param onAddTask Нажата кнопка "+"
 * @param onDragStart Начато перетаскивание карточки из этого квадранта
 * @param onDropHere Карточка отпущена в этот квадрант
 * @param onDropEnter Курсор вошёл в зону квадранта
 * @param onDropExit Курсор покинул зону квадранта
 * @param onDeleteRequest Запрос удаления задачи (свайп)
 * @param onEditRequest Запрос редактирования (долгое нажатие)
 * @param draggingTask Какая задача тащится сейчас (чтобы спрятать её карточку)
 */
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
    val quadrantColors = quadrant.colors()

    // Анимация рамки при наведении (hover во время DnD)
    val borderWidth by animateDpAsState(
        targetValue = if (isHovered) 2.dp else 0.dp,
        animationSpec = spring(),
        label = "border"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(quadrantColors.background)
            .then(
                if (isHovered) Modifier.border(
                    width = borderWidth,
                    color = quadrantColors.accent,
                    shape = RoundedCornerShape(12.dp)
                ) else Modifier
            )
            // Регистрируем квадрант как drop-target
            .dropTarget(
                onDragEnter = onDropEnter,
                onDragExit = onDropExit,
                onDrop = onDropHere
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ----- Заголовок квадранта -----
            QuadrantHeader(
                quadrant = quadrant,
                taskCount = tasks.size,
                quadrantColors = quadrantColors,
                onAddClick = onAddTask
            )

            // Подсветка зоны приёма при перетаскивании
            AnimatedVisibility(
                visible = isHovered,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(quadrantColors.accent.copy(alpha = 0.5f))
                )
            }

            // ----- Список задач -----
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp),
                contentPadding = PaddingValues(vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(
                    items = tasks,
                    key = { it.id }
                ) { task ->
                    // Скрываем карточку, которую тащат (показываем только призрак)
                    val isBeingDragged = task.id == draggingTask?.id
                    AnimatedVisibility(
                        visible = !isBeingDragged,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        TaskCard(
                            task = task,
                            isDragging = false,
                            onLongPress = { onEditRequest(task) },
                            onSwipeToDelete = { onDeleteRequest(task) },
                            // Modifier для области захвата (запускает drag)
                            dragHandleModifier = Modifier.draggable(
                                onDragStart = { onDragStart(task) },
                                onDragEnd = { /* handled by drop targets */ },
                                onDragCancel = { /* handled by ViewModel */ },
                                dragContent = {
                                    // Призрак — копия карточки (отображается под пальцем)
                                    TaskCard(
                                        task = task,
                                        isDragging = true,
                                        onLongPress = {},
                                        onSwipeToDelete = {}
                                    )
                                }
                            )
                        )
                    }
                }

                // Пустое состояние — подсказка
                if (tasks.isEmpty() && !isDragging) {
                    item {
                        EmptyQuadrantHint(quadrant = quadrant)
                    }
                }
            }
        }
    }
}

/**
 * Заголовок квадранта с иконкой, названием и кнопкой добавления.
 */
@Composable
private fun QuadrantHeader(
    quadrant: Quadrant,
    taskCount: Int,
    quadrantColors: com.eisenhower.matrix.ui.theme.QuadrantColors,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(quadrantColors.headerBackground)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Иконка квадранта
        Text(
            text = quadrant.icon(),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.width(6.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = quadrant.title,
                style = MaterialTheme.typography.headlineSmall,
                color = quadrantColors.accent,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = quadrant.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Счётчик задач
        if (taskCount > 0) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(quadrantColors.accent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = taskCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }
            Spacer(Modifier.width(4.dp))
        }

        // Кнопка добавления задачи
        IconButton(
            onClick = onAddClick,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Добавить задачу",
                tint = quadrantColors.accent,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Подсказка в пустом квадранте.
 */
@Composable
private fun EmptyQuadrantHint(quadrant: Quadrant) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Нажмите + чтобы добавить",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}
