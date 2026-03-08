package com.subbyte.subspectrum.ui.topbar.button

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.subbyte.subspectrum.base.SpectrumMachine
import com.subbyte.subspectrum.ui.components.IconButton

@Composable
fun ResetButton() {
    IconButton(
        tooltip = "Reset",
        onClick = {
            SpectrumMachine.reset()
        }
    ) {
        Icon(imageVector = Icons.Default.Replay, contentDescription = "Reset")
    }
}
