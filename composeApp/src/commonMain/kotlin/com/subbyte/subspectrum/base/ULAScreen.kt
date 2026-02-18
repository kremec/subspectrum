package com.subbyte.subspectrum.base

import androidx.compose.ui.graphics.Color
import com.subbyte.subspectrum.units.getBit
import kotlin.experimental.and

data class ULAPixelColors(
    val ink: Color,
    val paper: Color,
)

internal object ULAScreen {
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

    private val BORDER_IO_ADDRESS: IOAddress = 0x00FEu
    private const val BORDER_COLOR_MASK = 0b0000_0111

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

    fun isScreenFlashAttributeInverted(frameCount: Long): Boolean {
        val flashPhase = frameCount / FRAMES_PER_SCREEN_FLASH_ATTRIBUTE_PHASE
        val isOddPhase = flashPhase % 2L == 1L
        return isOddPhase
    }

    fun getCurrentBorderColor(): Color {
        val borderCode = IO.ioPortSet.getRawIO(BORDER_IO_ADDRESS).toInt() and BORDER_COLOR_MASK
        return getPaletteColor(borderCode, bright = false)
    }

    fun resolvePixelColors(attributeByte: Byte, flashPhaseOn: Boolean): ULAPixelColors {
        val flash = attributeByte.getBit(7)
        val bright = attributeByte.getBit(6)

        val paperCode = attributeByte.and(0b00111000).toInt() shr 3
        val paperColor = getPaletteColor(paperCode, bright)

        val inkCode = attributeByte.and(0b00000111).toInt()
        val inkColor = getPaletteColor(inkCode, bright)

        val effectiveInk = if (flash && flashPhaseOn) paperColor else inkColor
        val effectivePaper = if (flash && flashPhaseOn) inkColor else paperColor

        return ULAPixelColors(
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
