package com.subbyte.subspectrum.ui.topbar.button

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
import com.subbyte.subspectrum.base.SpectrumMachine
import com.subbyte.subspectrum.base.SpectrumRom
import com.subbyte.subspectrum.ui.components.IconButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoadRomMenu() {
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

    Box {
        IconButton(
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
            SpectrumRom.ROMS.forEach { (romName, romPath) ->
                DropdownMenuItem(
                    text = { Text(romName) },
                    onClick = {
                        showMenu = false
                        scope.launch(Dispatchers.Default) {
                            SpectrumMachine.loadRom(romPath)
                        }
                    }
                )
            }
        }
    }
}
