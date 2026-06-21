package com.eisenhower.matrix.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eisenhower.matrix.data.model.Task
import com.eisenhower.matrix.ui.theme.colors

/**
 * Карточка задачи с поддержкой:
 * - Свайпа влево/вправо для удаления
 * - Долгого нажатия для редактирования
 * - Визуальной обратной связи при перетаскивании
 *
 * @param task Отображаемая задача
 * @param isDragging Перетаскивается ли сейчас эта карточка
 * @param onLongPress Колбэк долгого нажатия → открыть редактор
 * @param onSwipeToDelete Колбэк запроса удаления
 * @param dragHandleModifier Modifier для области захвата (передаётся из drag & drop системы)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    task: Task,
    isDragging: Boolean = false,
    onLongPress: () -> Unit,
    onSwipeToDelete: () -> Unit,
    dragHandleModifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val quadrantColors = task.quadrant.colors()

    // Состояние свайпа (Material3 SwipeToDismiss)
    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.StartToEnd ||
                value == SwipeToDismissBoxValue.EndToStart
            ) {
                // Вибрация при подтверждении свайпа
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onSwipeToDelete()
                false // false = не удаляем сами, ждём подтверждения
            } else false
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.4f }
    )

    // Анимация тени при перетаскивании
    val elevation by animateDpAsState(
        targetValue = if (isDragging) 12.dp else 2.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "elevation"
    )

    // Масштаб при перетаскивании — карточка чуть увеличивается
    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    SwipeToDismissBox(
        state = swipeState,
        modifier = Modifier.fillMaxWidth(),
        backgroundContent = {
            // Красный фон при свайпе
            val color by animateColorAsState(
                targetValue = when (swipeState.dismissDirection) {
                    SwipeToDismissBoxValue.StartToEnd,
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFE53935)
                    else -> Color.Transparent
                },
                label = "swipe_bg"
            )
            val isActive = swipeState.dismissDirection != SwipeToDismissBoxValue.Settled

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = when (swipeState.dismissDirection) {
                    SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                    else -> Alignment.CenterEnd
                }
            ) {
                AnimatedVisibility(visible = isActive) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) {
        // Сама карточка задачи
        Card(
            modifier = dragHandleModifier
                .fillMaxWidth()
                .scale(scale)
                .shadow(elevation = elevation, shape = RoundedCornerShape(12.dp))
                .pointerInput(task) {
                    detectTapGestures(
                        onLongPress = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onLongPress()
                        }
                    )
                },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDragging) {
                    quadrantColors.background.copy(alpha = 0.9f)
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterStart
            ) {
                // Цветная полоска слева — индикатор квадранта
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(quadrantColors.accent)
                )

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (task.description.isNotBlank()) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
