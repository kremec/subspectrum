package com.subbyte.subspectrum.ui.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.subbyte.subspectrum.ui.topbar.button.KeyboardInputModeButton
import com.subbyte.subspectrum.ui.topbar.button.LoadProgramButton
import com.subbyte.subspectrum.ui.topbar.button.LoadRomMenu
import com.subbyte.subspectrum.ui.topbar.button.ProfilerButton
import com.subbyte.subspectrum.ui.topbar.button.ResetButton
import com.subbyte.subspectrum.ui.topbar.button.RunButton
import com.subbyte.subspectrum.ui.topbar.button.ScreenButton
import com.subbyte.subspectrum.ui.topbar.button.SoundButton
import com.subbyte.subspectrum.ui.topbar.button.SpeedButton
import com.subbyte.subspectrum.ui.topbar.button.StepButton

@Composable
fun TopBar() {
    MaterialTheme {
        Column(modifier = Modifier.background(Color.White)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LoadRomMenu()
                LoadProgramButton()
                Spacer(Modifier.width(20.dp))
                ResetButton()
                StepButton()
                RunButton()
                SpeedButton()
                Spacer(Modifier.width(20.dp))
                ScreenButton()
                KeyboardInputModeButton()
                SoundButton()
                Spacer(Modifier.weight(1f))
                ProfilerButton()
            }
        }
    }
}
