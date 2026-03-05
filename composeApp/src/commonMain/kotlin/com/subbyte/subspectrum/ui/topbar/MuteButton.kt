package com.subbyte.subspectrum.ui.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.subbyte.subspectrum.base.ULABeeper
import com.subbyte.subspectrum.ui.topbar.components.TopBarButton

@Composable
fun MuteButton() {
    val isMuted = ULABeeper.isMuted.value

    TopBarButton(
        tooltip = if (isMuted) "Unmute sound" else "Mute sound",
        onClick = { ULABeeper.toggleMuted() }
    ) {
        Icon(
            imageVector = if (isMuted) Icons.AutoMirrored.Outlined.VolumeOff else Icons.AutoMirrored.Outlined.VolumeUp,
            contentDescription = if (isMuted) "Unmute sound" else "Mute sound"
        )
    }
}