package com.subbyte.subspectrum.ui.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Monitor
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.subbyte.subspectrum.ui.window.ScreenWindowState
import com.subbyte.subspectrum.ui.topbar.components.TopBarButton

@Composable
fun ScreenButton() {
    TopBarButton(
        tooltip = if (ScreenWindowState.isOpen) "Close screen" else "Open screen",
        onClick = { ScreenWindowState.toggle() }
    ) {
        Icon(
            imageVector = Icons.Outlined.Monitor,
            contentDescription = if (ScreenWindowState.isOpen) "Close screen" else "Open screen"
        )
    }
}
