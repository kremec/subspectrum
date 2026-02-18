package com.subbyte.subspectrum.ui.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.base.ULAKeyboard
import com.subbyte.subspectrum.base.ULATiming
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.ui.topbar.components.TopBarButton

@Composable
fun ResetButton() {
    TopBarButton(
        tooltip = "Reset",
        onClick = {
            Processor.reset()
            Memory.memorySet.reset()
            Registers.normalRegisterSet.reset()
            Registers.shadowRegisterSet.reset()
            Registers.specialPurposeRegisters.reset()
            IO.ioPortSet.reset()
            ULATiming.reset()
            ULAKeyboard.releaseAllKeyboardKeys()
        }
    ) {
        Icon(imageVector = Icons.Default.Replay, contentDescription = "Reset")
    }
}