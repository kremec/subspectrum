package com.subbyte.subspectrum.base

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.subbyte.subspectrum.units.getBit
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.experimental.and

object ULA {
    const val CPU_CLOCK_HZ = 3_500_000
    const val T_STATES_PER_FRAME = 69888
    const val FRAMES_PER_SCREEN_FLASH_ATTRIBUTE_PHASE = 32

    const val FRAME_ATTRIBUTE_COLS = 32
    const val FRAME_ATTRIBUTE_ROWS = 24
    const val PIXELS_PER_ATTRIBUTE = 8

    const val FRAME_PIXEL_WIDTH = FRAME_ATTRIBUTE_COLS * PIXELS_PER_ATTRIBUTE
    const val FRAME_PIXEL_HEIGHT = FRAME_ATTRIBUTE_ROWS * PIXELS_PER_ATTRIBUTE

    const val FULL_SCREEN_PIXEL_WIDTH = 384
    const val FULL_SCREEN_PIXEL_HEIGHT = 288
    const val BORDER_PIXEL_WIDTH = (FULL_SCREEN_PIXEL_WIDTH - FRAME_PIXEL_WIDTH) / 2
    const val BORDER_PIXEL_HEIGHT = (FULL_SCREEN_PIXEL_HEIGHT - FRAME_PIXEL_HEIGHT) / 2

    const val DISPLAY_FILE_START = 0x4000
    const val DISPLAY_FILE_END = 0x57FF
    const val ATTRIBUTE_FILE_START = 0x5800
    const val ATTRIBUTE_FILE_END = 0x5AFF
    private const val ATTRIBUTE_ROWS_PER_SECTION = 8

    private const val BORDER_IO_PORT: UByte = 0xFEu
    private const val BORDER_COLOR_MASK = 0b0000_0111

    data class PixelColors(
        val ink: Color,
        val paper: Color,
    )

    private val colors = arrayOf(
        Pair(Color(0xFF000000), Color(0xFF000000)),
        Pair(Color(0xFF0100CE), Color(0xFF0200FD)),
        Pair(Color(0xFFCF0100), Color(0xFFFF0201)),
        Pair(Color(0xFFCF01CE), Color(0xFFFF02FD)),
        Pair(Color(0xFF00CF15), Color(0xFF00FF1C)),
        Pair(Color(0xFF01CFCF), Color(0xFF02FFFF)),
        Pair(Color(0xFFCFCF15), Color(0xFFFFFF1D)),
        Pair(Color(0xFFCFCFCF), Color(0xFFFFFFFF)),
    )

    var currentTStatesInFrame: Int = 0
        private set
    var totalTStates: Long = 0
        private set
    var frameCount: Long = 0
        private set

    private var interruptPending = false

    private val _isInstructionExecutionRealtime = mutableStateOf(false)
    val isInstructionExecutionRealtime: Boolean
        get() = _isInstructionExecutionRealtime.value

    private val _frameInvalidations = MutableSharedFlow<Long>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val frameInvalidations: SharedFlow<Long> = _frameInvalidations.asSharedFlow()
    init {
        _frameInvalidations.tryEmit(frameCount)
    }

    fun isInterruptPending(): Boolean {
        return interruptPending
    }

    fun clearInterrupt() {
        interruptPending = false
    }

    fun advanceCycles(cycles: Int) {
        val newTStatesInFrame = currentTStatesInFrame + cycles
        val frameNumber = newTStatesInFrame / T_STATES_PER_FRAME
        if (frameNumber > 0) {
            interruptPending = true
            frameCount += frameNumber.toLong()
            _frameInvalidations.tryEmit(frameCount)
        }
        currentTStatesInFrame = newTStatesInFrame % T_STATES_PER_FRAME
        totalTStates += cycles.toLong()
    }

    fun advanceNmiCycles() {
        advanceCycles(11)
    }

    fun advanceInterruptCycles(mode: Int) {
        val cycles = when (mode) {
            0 -> 0
            1 -> 13
            2 -> 19
            else -> 0
        }
        advanceCycles(cycles)
    }

    fun reset() {
        currentTStatesInFrame = 0
        totalTStates = 0
        frameCount = 0

        interruptPending = false

        _frameInvalidations.tryEmit(frameCount)
    }

    fun isScreenFlashAttributeInverted(): Boolean {
        val flashPhase = frameCount / FRAMES_PER_SCREEN_FLASH_ATTRIBUTE_PHASE
        val isOddPhase = flashPhase % 2L == 1L
        return isOddPhase
    }

    fun getCurrentBorderColor(): Color {
        val borderCode = IO.ioPortSet.getIOPort(BORDER_IO_PORT).toInt() and BORDER_COLOR_MASK
        return getPaletteColor(borderCode, bright = false)
    }

    fun resolvePixelColors(attributeByte: Byte, flashPhaseOn: Boolean): PixelColors {
        val flash = attributeByte.getBit(7)
        val bright = attributeByte.getBit(6)

        val paperCode = attributeByte.and(0b00111000).toInt() shr 3
        val paperColor = getPaletteColor(paperCode, bright)

        val inkCode = attributeByte.and(0b00000111).toInt()
        val inkColor = getPaletteColor(inkCode, bright)

        val effectiveInk = if (flash && flashPhaseOn) paperColor else inkColor
        val effectivePaper = if (flash && flashPhaseOn) inkColor else paperColor

        return PixelColors(
            ink = effectiveInk,
            paper = effectivePaper,
        )
    }

    fun getDisplayFileByteIndex(attributeRow: Int, attributeCol: Int, pixelRowInAttribute: Int): Int {
        val section = attributeRow / ATTRIBUTE_ROWS_PER_SECTION
        val rowInSection = attributeRow % ATTRIBUTE_ROWS_PER_SECTION

        val bytesPerSection = PIXELS_PER_ATTRIBUTE * ATTRIBUTE_ROWS_PER_SECTION * FRAME_ATTRIBUTE_COLS
        val scanlineBands = FRAME_ATTRIBUTE_COLS * ATTRIBUTE_ROWS_PER_SECTION

        return (
            (section * bytesPerSection) +
                (pixelRowInAttribute * scanlineBands) +
                (rowInSection * FRAME_ATTRIBUTE_COLS) +
                attributeCol
            )
    }

    private fun getPaletteColor(colorCode: Int, bright: Boolean): Color {
        val colorPair = colors[colorCode and BORDER_COLOR_MASK]
        return if (bright) colorPair.second else colorPair.first
    }
}
