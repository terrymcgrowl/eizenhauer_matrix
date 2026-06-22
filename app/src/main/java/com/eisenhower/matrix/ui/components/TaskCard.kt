package com.eisenhower.matrix.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eisenhower.matrix.data.model.Task
import com.eisenhower.matrix.ui.theme.colors
import java.text.SimpleDateFormat
import java.util.*

// Форматтер для отображения срока
private val dueDateFormatter = SimpleDateFormat("d MMM", Locale("ru"))

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
    val accent = task.quadrant.colors().accent

    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value != SwipeToDismissBoxValue.Settled) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onSwipeToDelete()
                false
            } else false
        },
        positionalThreshold = { it * 0.45f }
    )

    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.04f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    SwipeToDismissBox(
        state = swipeState,
        modifier = Modifier.fillMaxWidth(),
        backgroundContent = {
            val active = swipeState.dismissDirection != SwipeToDismissBoxValue.Settled
            val bgColor by animateColorAsState(
                targetValue = if (active) Color(0xFFD94F4F) else Color.Transparent,
                label = "swipe_bg"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(bgColor)
                    .padding(horizontal = 16.dp),
                contentAlignment = if (swipeState.dismissDirection == SwipeToDismissBoxValue.StartToEnd)
                    Alignment.CenterStart else Alignment.CenterEnd
            ) {
                if (active) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    ) {
        Surface(
            modifier = dragHandleModifier
                .fillMaxWidth()
                .scale(scale)
                .shadow(if (isDragging) 8.dp else 0.dp, RoundedCornerShape(8.dp))
                .pointerInput(task.id) {
                    detectTapGestures(onLongPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongPress()
                    })
                },
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Тонкая цветная полоска — единственный цветовой акцент
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(accent)
                )
                Spacer(Modifier.width(9.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    // Описание и срок — только если есть
                    val hasDescription = task.description.isNotBlank()
                    val hasDueDate = task.dueDate != null
                    if (hasDescription || hasDueDate) {
                        Spacer(Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (hasDescription) {
                                Text(
                                    text = task.description,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                            }
                            if (hasDueDate) {
                                val isOverdue = task.dueDate!! < System.currentTimeMillis()
                                Text(
                                    text = dueDateFormatter.format(Date(task.dueDate)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isOverdue) Color(0xFFD94F4F)
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}