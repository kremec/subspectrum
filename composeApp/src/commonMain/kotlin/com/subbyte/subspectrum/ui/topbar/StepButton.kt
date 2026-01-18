package com.subbyte.subspectrum.ui.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.LastPage
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.ui.topbar.components.TopBarButton
import kotlinx.coroutines.launch

@Composable
fun StepButton() {
    val scope = rememberCoroutineScope()

    TopBarButton(
        tooltip = "Step",
        onClick = { scope.launch { Processor.step() } }
    ) {
        Icon(imageVector = Icons.AutoMirrored.Outlined.LastPage, contentDescription = "Step")
    }
}