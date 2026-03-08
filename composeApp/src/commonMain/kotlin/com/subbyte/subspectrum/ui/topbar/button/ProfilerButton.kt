package com.subbyte.subspectrum.ui.topbar.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.ui.components.IconButton
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.TimeSource

@Composable
fun ProfilerButton() {
    var showMenu by remember { mutableStateOf(false) }
    var instructionsPerSecond by remember { mutableStateOf(0.0) }

    LaunchedEffect(showMenu) {
        if (!showMenu) {
            return@LaunchedEffect
        }

        var previousInstructionCount = Processor.totalInstructionsExecuted
        var previousSampleTime = TimeSource.Monotonic.markNow()

        while (true) {
            delay(1000)

            val sampleSeconds = previousSampleTime.elapsedNow().inWholeNanoseconds / 1_000_000_000.0
            if (sampleSeconds <= 0.0) {
                continue
            }

            val currentInstructionCount = Processor.totalInstructionsExecuted
            val instructionDelta = (currentInstructionCount - previousInstructionCount).coerceAtLeast(0)
            instructionsPerSecond = instructionDelta / sampleSeconds
            previousInstructionCount = currentInstructionCount
            previousSampleTime = TimeSource.Monotonic.markNow()
        }
    }

    Box {
        IconButton(
            tooltip = "Profiler",
            onClick = { showMenu = !showMenu }
        ) {
            Icon(imageVector = Icons.Outlined.BugReport, contentDescription = "Profiler")
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = {},
            containerColor = Color.White,
            shadowElevation = 0.dp,
            border = BorderStroke(1.dp, Color.Black),
            properties = PopupProperties(focusable = false)
        ) {
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                Text(text = "Instructions/s: ${formatInstructionsPerSecond(instructionsPerSecond)}")
            }
        }
    }
}

private fun formatInstructionsPerSecond(value: Double): String {
    val absValue = abs(value)
    return when {
        absValue >= 1_000_000.0 -> "${formatTwoDecimals(value / 1_000_000.0)}M"
        absValue >= 1_000.0 -> "${formatTwoDecimals(value / 1_000.0)}k"
        else -> formatTwoDecimals(value)
    }
}

private fun formatTwoDecimals(value: Double): String {
    val roundedHundredth = (value * 100.0).roundToInt()
    val wholePart = roundedHundredth / 100
    val decimalPart = abs(roundedHundredth % 100)
    return "$wholePart.${decimalPart.toString().padStart(2, '0')}"
}
