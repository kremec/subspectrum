package com.subbyte.subspectrum

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.subbyte.subspectrum.ui.topbar.TopBar

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "subspectrum",
    ) {
        window.rootPane.apply {
            rootPane.putClientProperty("apple.awt.fullWindowContent", true)
            rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
        }

        Column {
            TopBar()
            App()
        }
    }
}