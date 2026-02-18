package com.subbyte.subspectrum

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.subbyte.subspectrum.base.ULAKeyboard
import com.subbyte.subspectrum.ui.topbar.TopBar
import com.subbyte.subspectrum.ui.window.ScreenWindowContent
import com.subbyte.subspectrum.ui.window.ScreenWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "subspectrum",
    ) {
        window.rootPane.apply {
            rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
        }

        Column {
            TopBar()
            App()
        }
    }

    if (ScreenWindowState.isOpen) {
        val keyPulseScope = rememberCoroutineScope()

        Window(
            onCloseRequest = {
                ULAKeyboard.releaseAllKeyboardKeys()
                ScreenWindowState.close()
            },
            title = "subspectrum screen",
            onPreviewKeyEvent = { event -> ULAKeyboard.handlePreviewKeyEvent(event, keyPulseScope) }
        ) {
            window.rootPane.apply {
                rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
            }

            ScreenWindowContent()
        }
    }
}
