package com.subbyte.subspectrum.ui.topbar.button

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Gradient
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.subbyte.subspectrum.base.ULAKeyboard
import com.subbyte.subspectrum.base.ULAKeyboardInputMode
import com.subbyte.subspectrum.ui.components.IconButton

@Composable
fun KeyboardInputModeButton() {
    val isActualMode = ULAKeyboard.keyboardInputMode.value == ULAKeyboardInputMode.Actual

    IconButton(
        tooltip = if (isActualMode) "Keyboard: Host" else "Keyboard: ZX",
        onClick = { ULAKeyboard.toggleKeyboardInputMode() }
    ) {
        Icon(
            imageVector = if (isActualMode) Icons.Outlined.Keyboard else Icons.Outlined.Gradient,
            contentDescription = if (isActualMode) "Keyboard: Host" else "Keyboard: ZX"
        )
    }
}
