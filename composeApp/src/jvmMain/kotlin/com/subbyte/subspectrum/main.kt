package com.subbyte.subspectrum

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.subbyte.subspectrum.ui.window.ScreenWindowContent
import com.subbyte.subspectrum.ui.window.ScreenWindowState
import com.subbyte.subspectrum.ui.topbar.TopBar

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
        Window(
            onCloseRequest = ScreenWindowState::close,
            title = "subspectrum screen",
        ) {
            window.rootPane.apply {
                rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
            }

            ScreenWindowContent()
        }
    }
}
