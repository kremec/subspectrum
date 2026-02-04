package com.subbyte.subspectrum.ui.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.ui.topbar.components.TopBarButton
import kotlinx.coroutines.launch

@Composable
fun LoadProgramButton() {
    val scope = rememberCoroutineScope()

    TopBarButton(
        tooltip = "Load Program",
        onClick = {
            scope.launch {
                val instructionBytes = byteArrayOf(
                    0x7E.toByte()
                )
                Memory.memorySet.setMemoryCells(0.toUShort(), instructionBytes)
            }
        }
    ) {
        Icon(imageVector = Icons.Outlined.UploadFile, contentDescription = "Load Program")
    }
}