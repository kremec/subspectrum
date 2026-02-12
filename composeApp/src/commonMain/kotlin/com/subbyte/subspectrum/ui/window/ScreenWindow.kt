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
import kotlin.experimental.and

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
    val ROWS = 24
    val ROWS_PER_SECTION = 8
    val COLS = 32
    val PIXELS_PER_LOCATION = 8

    val COLOR_BLACK = Color(0xFF000000)
    val COLOR_BLACK_BRIGHT = Color(0xFF000000)
    val COLOR_BLUE = Color(0xFF0100CE)
    val COLOR_BLUE_BRIGHT = Color(0xFF0200FD)
    val COLOR_RED = Color(0xFFCF0100)
    val COLOR_RED_BRIGHT = Color(0xFFFF0201)
    val COLOR_MAGENTA = Color(0xFFCF01CE)
    val COLOR_MAGENTA_BRIGHT = Color(0xFFFF02FD)
    val COLOR_GREEN = Color(0xFF00CF15)
    val COLOR_GREEN_BRIGHT = Color(0xFF00FF1C)
    val COLOR_CYAN = Color(0xFF01CFCF)
    val COLOR_CYAN_BRIGHT = Color(0xFF02FFFF)
    val COLOR_YELLOW = Color(0xFFCFCF15)
    val COLOR_YELLOW_BRIGHT = Color(0xFFFFFF1D)
    val COLOR_WHITE = Color(0xFFCFCFCF)
    val COLOR_WHITE_BRIGHT = Color(0xFFFFFFFF)
    val COLORS = arrayOf(
        Pair(COLOR_BLACK, COLOR_BLACK_BRIGHT),
        Pair(COLOR_BLUE, COLOR_BLUE_BRIGHT),
        Pair(COLOR_RED, COLOR_RED_BRIGHT),
        Pair(COLOR_MAGENTA, COLOR_MAGENTA_BRIGHT),
        Pair(COLOR_GREEN, COLOR_GREEN_BRIGHT),
        Pair(COLOR_CYAN, COLOR_CYAN_BRIGHT),
        Pair(COLOR_YELLOW, COLOR_YELLOW_BRIGHT),
        Pair(COLOR_WHITE, COLOR_WHITE_BRIGHT)
    )

    var frameVersion by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        ULA.frameInvalidations
            .conflate()
            .collect { frameVersion++ }
    }

    val displayFile = remember(frameVersion) {
        Memory.memorySet.getMemoryCells(0x4000.toUShort(), 0x57FF.toUShort())
    }
    val attributes = remember(frameVersion) {
        Memory.memorySet.getMemoryCells(0x5800.toUShort(), 0x5AFF.toUShort())
    }
    val flashPhaseOn = ULA.isScreenFlashAttributeInverted()

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        val totalPixelWidth = COLS * PIXELS_PER_LOCATION
        val totalPixelHeight = ROWS * PIXELS_PER_LOCATION

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
            // Draw each individual pixel
            for (row in 0 until ROWS) {
                for (col in 0 until COLS) {

                    val attributeByteIndex = row * COLS + col
                    val attributeByteValue = attributes[attributeByteIndex]

                    val flash = attributeByteValue.getBit(7)

                    val bright = attributeByteValue.getBit(6)

                    val paperCode = attributeByteValue.and(0b00111000).toInt() shr 3
                    val paperColor = if (bright) COLORS[paperCode].second else COLORS[paperCode].first

                    val inkCode = attributeByteValue.and(0b00000111).toInt()
                    val inkColor = if (bright) COLORS[inkCode].second else COLORS[inkCode].first
                    val effectiveInkColor = if (flash && flashPhaseOn) paperColor else inkColor
                    val effectivePaperColor = if (flash && flashPhaseOn) inkColor else paperColor

                    for (py in 0 until PIXELS_PER_LOCATION) {
                        for (px in 0 until PIXELS_PER_LOCATION) {
                            val absX = col * PIXELS_PER_LOCATION + px
                            val absY = row * PIXELS_PER_LOCATION + py

                            val section = row / ROWS_PER_SECTION // 0 = top, 1 = middle, 2 = bottom
                            val rowInSection = row % ROWS_PER_SECTION
                            val scanline = py

                            val bytesPerSection = PIXELS_PER_LOCATION * ROWS_PER_SECTION * COLS
                            val scanlineBands = COLS * ROWS_PER_SECTION
                            val byteIndex =
                                (section * bytesPerSection) + (scanline * scanlineBands) + (rowInSection * COLS) + col

                            val byteValue = displayFile[byteIndex]
                            val bitIndex = 7 - px // Bit 7 = leftmost pixel, Bit 0 = rightmost

                            val isInk = byteValue.getBit(bitIndex)

                            drawRect(
                                color = if (isInk) effectiveInkColor else effectivePaperColor,
                                topLeft = Offset(
                                    absX * pixelSize.toFloat(),
                                    absY * pixelSize.toFloat()
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
