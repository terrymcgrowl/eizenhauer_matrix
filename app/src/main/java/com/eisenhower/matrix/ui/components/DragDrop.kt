package com.eisenhower.matrix.ui.components

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntSize

/**
 * Внутреннее состояние системы Drag & Drop.
 * Хранит всё необходимое для корректного позиционирования тени.
 */
internal data class DragState(
    // Перемещение от начальной точки (offset курсора)
    val dragOffset: Offset = Offset.Zero,
    // Абсолютная позиция точки захвата в окне
    val draggableComposableOffset: Offset = Offset.Zero,
    // Размер перетаскиваемой карточки
    val draggableComposableSize: IntSize = IntSize.Zero,
    // Идёт ли перетаскивание
    val isDragging: Boolean = false,
    // Composable-тень (то, что отображается под пальцем)
    val draggableContent: (@Composable () -> Unit)? = null
)

/**
 * Composition Local для передачи состояния DnD вниз по дереву компонентов.
 * Позволяет любому дочернему Composable получить/изменить состояние DnD.
 */
internal val LocalDragState = compositionLocalOf { mutableStateOf(DragState()) }

/**
 * Контейнер верхнего уровня, обеспечивающий работу Drag & Drop.
 * Все перетаскиваемые и целевые элементы должны быть вложены в этот контейнер.
 *
 * Рисует "тень" (ghostкарточку) поверх всего содержимого во время перетаскивания.
 */
@Composable
fun DragDropContainer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val state = remember { mutableStateOf(DragState()) }

    CompositionLocalProvider(LocalDragState provides state) {
        Box(modifier = modifier) {
            content()

            // Отображаем "призрак" карточки под пальцем во время перетаскивания
            if (state.value.isDragging) {
                state.value.draggableContent?.let { dragContent ->
                    // Позиция: начальная позиция карточки + смещение пальца
                    val ghostX = state.value.draggableComposableOffset.x + state.value.dragOffset.x
                    val ghostY = state.value.draggableComposableOffset.y + state.value.dragOffset.y
                    Box(
                        modifier = Modifier.graphicsLayer {
                            translationX = ghostX
                            translationY = ghostY
                            // Небольшой наклон для ощущения "подъёма"
                            rotationZ = 2f
                            alpha = 0.92f
                            scaleX = 1.04f
                            scaleY = 1.04f
                            shadowElevation = 24f
                        }
                    ) {
                        dragContent()
                    }
                }
            }
        }
    }
}

/**
 * Modifier для перетаскиваемого элемента.
 * Запускает drag после долгого нажатия (detectDragGesturesAfterLongPress).
 *
 * @param task Перетаскиваемый объект (передаётся через колбэки)
 * @param onDragStart Колбэк начала перетаскивания
 * @param onDragEnd Колбэк завершения
 * @param onDragCancel Колбэк отмены
 * @param dragContent Composable-содержимое "призрака"
 */
@Composable
fun Modifier.draggable(
    onDragStart: () -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDragCancel: () -> Unit = {},
    dragContent: @Composable () -> Unit
): Modifier {
    val dragState = LocalDragState.current
    val haptic = LocalHapticFeedback.current
    var itemOffset by remember { mutableStateOf(Offset.Zero) }
    var itemSize by remember { mutableStateOf(IntSize.Zero) }

    return this
        // Запоминаем координаты элемента в окне
        .onGloballyPositioned { coords ->
            itemOffset = coords.boundsInWindow().topLeft
            itemSize = coords.size
        }
        // Длинное нажатие → начинаем перетаскивание
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    // Хаптик при начале перетаскивания
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    dragState.value = dragState.value.copy(
                        isDragging = true,
                        dragOffset = Offset.Zero,
                        draggableComposableOffset = itemOffset,
                        draggableComposableSize = itemSize,
                        draggableContent = dragContent
                    )
                    onDragStart()
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    // Накапливаем смещение пальца
                    dragState.value = dragState.value.copy(
                        dragOffset = dragState.value.dragOffset + dragAmount
                    )
                },
                onDragEnd = {
                    dragState.value = dragState.value.copy(isDragging = false)
                    onDragEnd()
                },
                onDragCancel = {
                    dragState.value = dragState.value.copy(isDragging = false)
                    onDragCancel()
                }
            )
        }
}

/**
 * Modifier для области-приёмника перетаскивания (квадрант матрицы).
 * Отслеживает, находится ли "призрак" над этой областью.
 *
 * @param onDragEnter Колбэк — курсор вошёл в эту область
 * @param onDragExit Колбэк — курсор покинул область
 * @param onDrop Колбэк — отпустили в этой области
 */
@Composable
fun Modifier.dropTarget(
    onDragEnter: () -> Unit = {},
    onDragExit: () -> Unit = {},
    onDrop: () -> Unit = {}
): Modifier {
    val dragState = LocalDragState.current
    val haptic = LocalHapticFeedback.current
    var dropBounds by remember { mutableStateOf(androidx.compose.ui.geometry.Rect.Zero) }
    var isHovered by remember { mutableStateOf(false) }

    // Следим за положением курсора относительно этой области
    LaunchedEffect(
        dragState.value.isDragging,
        dragState.value.dragOffset,
        dragState.value.draggableComposableOffset
    ) {
        if (!dragState.value.isDragging) {
            if (isHovered) {
                onDrop()
                isHovered = false
            }
            return@LaunchedEffect
        }

        // Центр "призрака" в координатах окна
        val ghostCenter = Offset(
            x = dragState.value.draggableComposableOffset.x +
                    dragState.value.dragOffset.x +
                    dragState.value.draggableComposableSize.width / 2f,
            y = dragState.value.draggableComposableOffset.y +
                    dragState.value.dragOffset.y +
                    dragState.value.draggableComposableSize.height / 2f
        )

        val nowHovered = dropBounds.contains(ghostCenter)
        if (nowHovered && !isHovered) {
            // Вошли в зону — хаптик "щелчок"
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onDragEnter()
        } else if (!nowHovered && isHovered) {
            onDragExit()
        }
        isHovered = nowHovered
    }

    return this.onGloballyPositioned { coords ->
        dropBounds = coords.boundsInWindow()
    }
}
