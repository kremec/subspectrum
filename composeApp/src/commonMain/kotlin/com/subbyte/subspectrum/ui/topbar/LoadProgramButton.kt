package com.subbyte.subspectrum.ui.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.subbyte.subspectrum.ui.topbar.components.TopBarButton

@Composable
fun LoadProgramButton() {
    TopBarButton(
        tooltip = "Load Program",
        onClick = { }
    ) {
        Icon(imageVector = Icons.Outlined.UploadFile, contentDescription = "Load Program")
    }
}