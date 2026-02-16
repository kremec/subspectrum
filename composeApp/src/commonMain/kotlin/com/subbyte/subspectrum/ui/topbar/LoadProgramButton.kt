package com.subbyte.subspectrum.ui.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.ui.topbar.components.TopBarButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val DISPLAY_FILE_ADDRESS = 0x4000
private const val DISPLAY_FILE_SIZE = 6144
private const val ATTRIBUTE_FILE_ADDRESS = 0x5800
private const val ATTRIBUTE_COLUMNS = 32
private const val ATTRIBUTE_ROWS = 24
private const val ATTRIBUTE_FILE_SIZE = ATTRIBUTE_COLUMNS * ATTRIBUTE_ROWS

@Composable
fun LoadProgramButton() {
    val scope = rememberCoroutineScope()

    TopBarButton(
        tooltip = "Load Program",
        onClick = {
            scope.launch(Dispatchers.Default) {
                val displayFile = ByteArray(DISPLAY_FILE_SIZE) { index ->
                    val scanline = (index % 256) / 32 // Scanline inside an 8-pixel character row (0-7)
                    val column = index % ATTRIBUTE_COLUMNS
                    if ((scanline + column) % 2 == 0) 0b10101010.toByte() else 0b01010101.toByte()
                }

                val attributes = ByteArray(ATTRIBUTE_FILE_SIZE) { index ->
                    val row = index / ATTRIBUTE_COLUMNS
                    val col = index % ATTRIBUTE_COLUMNS

                    val ink = col % 8
                    val paper = (row / 3) % 8
                    val bright = (row % 2 == 1)
                    val flash = ((col / 4) + (row / 3)) % 2 == 1

                    val brightBit = if (bright) 0b01000000 else 0
                    val flashBit = if (flash) 0b10000000 else 0
                    (flashBit or brightBit or (paper shl 3) or ink).toByte()
                }

                Memory.memorySet.setMemoryCells(DISPLAY_FILE_ADDRESS.toUShort(), displayFile)
                Memory.memorySet.setMemoryCells(ATTRIBUTE_FILE_ADDRESS.toUShort(), attributes)
            }
        }
    ) {
        Icon(imageVector = Icons.Outlined.UploadFile, contentDescription = "Load Program")
    }
}