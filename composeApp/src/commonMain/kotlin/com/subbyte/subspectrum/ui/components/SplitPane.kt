package com.subbyte.subspectrum.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalSplitPane(
    modifier: Modifier = Modifier,
    initialSplit: Float = 0.5f,
    minSize: Float = 100f,
    left: @Composable () -> Unit,
    right: @Composable () -> Unit
) {
    var splitRatio by remember { mutableStateOf(initialSplit) }
    var totalWidth by remember { mutableFloatStateOf(0f) }

    Row(modifier = modifier.onGloballyPositioned { coordinates ->
        totalWidth = coordinates.size.width.toFloat()
    }) {
        Box(
            modifier = Modifier
                .weight(splitRatio)
                .fillMaxHeight()
        ) {
            left()
        }

        // Divider
        Box(
            modifier = Modifier
                .width(2.dp)
                .fillMaxHeight()
                .background(Color.Gray)
                .pointerHoverIcon(PointerIcon.Hand)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val newRatio = splitRatio + (dragAmount.x / totalWidth)
                        splitRatio = newRatio.coerceIn(minSize / totalWidth, 1f - minSize / totalWidth)
                    }
                }
        )

        Box(
            modifier = Modifier
                .weight(1f - splitRatio)
                .fillMaxHeight()
        ) {
            right()
        }
    }
}

@Composable
fun VerticalSplitPane(
    modifier: Modifier = Modifier,
    initialSplit: Float = 0.5f,
    minSize: Float = 100f,
    top: @Composable () -> Unit,
    bottom: @Composable () -> Unit
) {
    var splitRatio by remember { mutableStateOf(initialSplit) }
    var totalHeight by remember { mutableFloatStateOf(0f) }

    Column(modifier = modifier.onGloballyPositioned { coordinates ->
        totalHeight = coordinates.size.height.toFloat()
    }) {
        Box(
            modifier = Modifier
                .weight(splitRatio)
                .fillMaxWidth()
        ) {
            top()
        }

        // Divider
        Box(
            modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .background(Color.Gray)
                .pointerHoverIcon(PointerIcon.Hand)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val newRatio = splitRatio + (dragAmount.y / totalHeight)
                        splitRatio = newRatio.coerceIn(minSize / totalHeight, 1f - minSize / totalHeight)
                    }
                }
        )

        Box(
            modifier = Modifier
                .weight(1f - splitRatio)
                .fillMaxWidth()
        ) {
            bottom()
        }
    }
}