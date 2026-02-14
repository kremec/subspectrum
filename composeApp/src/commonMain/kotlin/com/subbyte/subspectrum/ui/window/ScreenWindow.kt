package com.subbyte.subspectrum.ui.window

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.ULA
import com.subbyte.subspectrum.units.getBit
import kotlinx.coroutines.flow.conflate

object ScreenWindowState {
    private val _isOpen: MutableState<Boolean> = mutableStateOf(false)

    val isOpen: Boolean
        get() = _isOpen.value

    fun close() {
        _isOpen.value = false
    }

    fun toggle() {
        _isOpen.value = !_isOpen.value
    }
}

@Composable
fun ScreenWindowContent() {
    var frameVersion by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        ULA.frameInvalidations
            .conflate()
            .collect { frameVersion++ }
    }

    val displayFile = remember(frameVersion) {
        Memory.memorySet.getMemoryCells(
            ULA.DISPLAY_FILE_START.toUShort(),
            ULA.DISPLAY_FILE_END.toUShort(),
        )
    }
    val attributes = remember(frameVersion) {
        Memory.memorySet.getMemoryCells(
            ULA.ATTRIBUTE_FILE_START.toUShort(),
            ULA.ATTRIBUTE_FILE_END.toUShort(),
        )
    }
    val flashPhaseOn = ULA.isScreenFlashAttributeInverted()
    val borderColor = ULA.getCurrentBorderColor()

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        val totalPixelWidth = ULA.FULL_SCREEN_PIXEL_WIDTH
        val totalPixelHeight = ULA.FULL_SCREEN_PIXEL_HEIGHT

        // Calculate integer pixel size (no fractions = no gaps)
        val pixelSize = minOf(
            constraints.maxWidth / totalPixelWidth,
            constraints.maxHeight / totalPixelHeight
        ).coerceAtLeast(1)  // Ensure at least 1px

        val gridWidth = pixelSize * totalPixelWidth
        val gridHeight = pixelSize * totalPixelHeight

        Canvas(
            modifier = Modifier.size(
                with(LocalDensity.current) { gridWidth.toDp() },
                with(LocalDensity.current) { gridHeight.toDp() }
            )
        ) {
            drawRect(
                color = borderColor,
                topLeft = Offset.Zero,
                size = size,
            )

            // Draw each individual pixel
            for (row in 0 until ULA.FRAME_ATTRIBUTE_ROWS) {
                for (col in 0 until ULA.FRAME_ATTRIBUTE_COLS) {

                    val attributeByteIndex = row * ULA.FRAME_ATTRIBUTE_COLS + col
                    val attributeByteValue = attributes[attributeByteIndex]

                    val pixelColors = ULA.resolvePixelColors(attributeByteValue, flashPhaseOn)

                    for (py in 0 until ULA.PIXELS_PER_ATTRIBUTE) {
                        for (px in 0 until ULA.PIXELS_PER_ATTRIBUTE) {
                            val absX = col * ULA.PIXELS_PER_ATTRIBUTE + px
                            val absY = row * ULA.PIXELS_PER_ATTRIBUTE + py

                            val byteIndex = ULA.getDisplayFileByteIndex(
                                attributeRow = row,
                                attributeCol = col,
                                pixelRowInAttribute = py,
                            )

                            val byteValue = displayFile[byteIndex]
                            val bitIndex = 7 - px // Bit 7 = leftmost pixel, Bit 0 = rightmost

                            val isInk = byteValue.getBit(bitIndex)

                            drawRect(
                                color = if (isInk) pixelColors.ink else pixelColors.paper,
                                topLeft = Offset(
                                    (ULA.BORDER_PIXEL_WIDTH + absX) * pixelSize.toFloat(),
                                    (ULA.BORDER_PIXEL_HEIGHT + absY) * pixelSize.toFloat(),
                                ),
                                size = Size(pixelSize.toFloat(), pixelSize.toFloat())
                            )
                        }
                    }
                }
            }
        }
    }
}
