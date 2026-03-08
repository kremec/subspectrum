package com.subbyte.subspectrum.ui.topbar.button

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.LastPage
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.ui.components.IconButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun StepButton() {
    val scope = rememberCoroutineScope()

    IconButton(
        tooltip = "Step",
        onClick = { scope.launch(Dispatchers.Default) { Processor.step() } }
    ) {
        Icon(imageVector = Icons.AutoMirrored.Outlined.LastPage, contentDescription = "Step")
    }
}