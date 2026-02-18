package com.subbyte.subspectrum.ui.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
                Spacer(Modifier.width(20.dp))
                ScreenButton()
                KeyboardInputModeButton()
            }
        }
    }
}