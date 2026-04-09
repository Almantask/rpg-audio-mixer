package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex

/**
 * Makes a composable draggable after a long press.
 * Used for drag-to-reorder functionality in lists and grids.
 */
@Composable
fun <T> DraggableItem(
    item: T,
    isDragging: Boolean,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (Offset) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isDraggingLocal by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .zIndex(if (isDragging || isDraggingLocal) 1f else 0f)
            .graphicsLayer {
                if (isDragging || isDraggingLocal) {
                    translationX = dragOffset.x
                    translationY = dragOffset.y
                    alpha = 0.8f
                    scaleX = 1.05f
                    scaleY = 1.05f
                }
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        isDraggingLocal = true
                        dragOffset = Offset.Zero
                        onDragStart()
                    },
                    onDragEnd = {
                        isDraggingLocal = false
                        dragOffset = Offset.Zero
                        onDragEnd()
                    },
                    onDragCancel = {
                        isDraggingLocal = false
                        dragOffset = Offset.Zero
                        onDragEnd()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount
                        onDrag(dragAmount)
                    }
                )
            }
    ) {
        content(item)
    }
}
