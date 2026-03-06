package com.subbyte.subspectrum.ui.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.subbyte.subspectrum.base.ULATiming
import com.subbyte.subspectrum.ui.topbar.components.TopBarButton

@Composable
fun SpeedButton() {
    val isFastExecution = ULATiming.isInstructionExecutionRealtime.value

    TopBarButton(
        tooltip = if (isFastExecution) "Speed: Fast" else "Speed: Normal",
        onClick = {
            ULATiming.isInstructionExecutionRealtime.value = !isFastExecution
        }
    ) {
        Icon(
            imageVector = if (isFastExecution) Icons.Outlined.Speed else Icons.Outlined.Schedule,
            contentDescription = if (isFastExecution) "Speed: Fast" else "Speed: Normal",
        )
    }
}
