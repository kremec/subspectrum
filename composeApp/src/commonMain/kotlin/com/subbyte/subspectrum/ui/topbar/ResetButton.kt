package com.subbyte.subspectrum.ui.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.base.ULA
import com.subbyte.subspectrum.ui.topbar.components.TopBarButton

@Composable
fun ResetButton() {
    TopBarButton(
        tooltip = "Reset",
        onClick = {
            Memory.memorySet.reset()
            Registers.normalRegisterSet.reset()
            Registers.shadowRegisterSet.reset()
            Registers.specialPurposeRegisters.reset()
            IO.ioPortSet.reset()
            ULA.reset()
        }
    ) {
        Icon(imageVector = Icons.Default.Replay, contentDescription = "Reset")
    }
}