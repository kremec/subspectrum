package com.subbyte.subspectrum.ui.topbar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.InstallDesktop
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.ui.topbar.components.TopBarButton
import kotlinx.coroutines.launch
import subspectrum.composeapp.generated.resources.Res

@Composable
fun LoadRomMenu() {
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

    val roms = listOf(
        Pair("48kB", "files/roms/48.rom")
    )

    Box {
        TopBarButton(
            tooltip = "Load ROM",
            onClick = { showMenu = true }
        ) {
            Icon(imageVector = Icons.Outlined.InstallDesktop, contentDescription = "Load ROM")
        }
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            containerColor = Color.White,
            shadowElevation = 0.dp,
            border = BorderStroke(1.dp, Color.Black)
        ) {
            roms.forEach { (romName, romPath) ->
                DropdownMenuItem(
                    text = { Text(romName) },
                    onClick = {
                        showMenu = false
                        scope.launch {
                            val bytes = Res.readBytes(romPath)
                            Memory.memorySet.setMemoryCells(0.toUShort(), bytes)
                        }
                    }
                )
            }
        }
    }
}