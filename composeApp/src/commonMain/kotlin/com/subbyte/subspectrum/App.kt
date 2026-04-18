package com.subbyte.subspectrum

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.subbyte.subspectrum.base.SpectrumMachine
import com.subbyte.subspectrum.ui.panel.DisassemblyPanel
import com.subbyte.subspectrum.ui.panel.MemoryPanel
import com.subbyte.subspectrum.ui.panel.RegistersPanel
import com.subbyte.subspectrum.ui.components.HorizontalSplitPane
import com.subbyte.subspectrum.ui.components.VerticalSplitPane

@Composable
fun App() {
    LaunchedEffect(Unit) {
        SpectrumMachine.loadRom()
    }

    MaterialTheme {
        HorizontalSplitPane(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            initialSplit = 0.5f,
            left = {
                VerticalSplitPane(
                    modifier = Modifier.fillMaxSize(),
                    initialSplit = 0.5f,
                    top = { RegistersPanel() },
                    bottom = { MemoryPanel() }
                )
            },
            right = {
                DisassemblyPanel()
            }
        )
    }
}