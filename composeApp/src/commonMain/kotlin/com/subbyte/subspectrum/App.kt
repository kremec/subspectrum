package com.subbyte.subspectrum

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.subbyte.subspectrum.ui.panel.DisassemblyPanel
import com.subbyte.subspectrum.ui.panel.MemoryPanel
import com.subbyte.subspectrum.ui.panel.RegistersPanel
import com.subbyte.subspectrum.ui.panel.WatchPanel
import com.subbyte.subspectrum.ui.panel.components.HorizontalSplitPane
import com.subbyte.subspectrum.ui.panel.components.VerticalSplitPane
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        HorizontalSplitPane(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            initialSplit = 0.4f,
            left = {
                VerticalSplitPane(
                    modifier = Modifier.fillMaxSize(),
                    initialSplit = 0.5f,
                    top = { RegistersPanel() },
                    bottom = { DisassemblyPanel() }
                )
            },
            right = {
                VerticalSplitPane(
                    modifier = Modifier.fillMaxSize(),
                    initialSplit = 0.5f,
                    top = { WatchPanel() },
                    bottom = { MemoryPanel() }
                )
            }
        )
    }
}