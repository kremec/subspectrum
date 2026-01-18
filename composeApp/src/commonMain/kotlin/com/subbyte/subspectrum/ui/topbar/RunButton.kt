package com.subbyte.subspectrum.ui.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.ui.topbar.components.TopBarButton
import kotlinx.coroutines.launch

@Composable
fun RunButton() {
    val scope = rememberCoroutineScope()

    TopBarButton(
        tooltip = if (Processor.running) "Stop" else "Run",
        onClick = { scope.launch { if (Processor.running) Processor.stop() else Processor.run() } }
    ) {
        Icon(imageVector = if (Processor.running) Icons.Outlined.Stop else Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = if (Processor.running) "Stop" else "Run")
    }
}