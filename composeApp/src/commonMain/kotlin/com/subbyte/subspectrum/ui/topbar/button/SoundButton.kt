package com.subbyte.subspectrum.ui.topbar.button

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.subbyte.subspectrum.base.ULABeeper
import com.subbyte.subspectrum.ui.components.IconButton

@Composable
fun SoundButton() {
    val isMuted = ULABeeper.isMuted.value

    IconButton(
        tooltip = if (isMuted) "Sound: Off" else "Sound: On",
        onClick = { ULABeeper.toggleMuted() }
    ) {
        Icon(
            imageVector = if (isMuted) Icons.AutoMirrored.Outlined.VolumeOff else Icons.AutoMirrored.Outlined.VolumeUp,
            contentDescription = if (isMuted) "Sound: Off" else "Sound: On"
        )
    }
}