package com.subbyte.subspectrum.ui.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.subbyte.subspectrum.ProgramFilePicker
import com.subbyte.subspectrum.base.ULATapeDeck
import com.subbyte.subspectrum.base.ULATapeParser
import com.subbyte.subspectrum.ui.topbar.components.TopBarButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoadProgramButton() {
    val scope = rememberCoroutineScope()

    TopBarButton(
        tooltip = "Load Tape",
        onClick = {
            scope.launch {
                val selectedFileBytes = ProgramFilePicker.pickTapeProgramBytes() ?: return@launch
                withContext(Dispatchers.Default) {
                    val tape = ULATapeParser.parse(selectedFileBytes)
                    ULATapeDeck.insertTape(tape)
                }
            }
        }
    ) {
        Icon(imageVector = Icons.Outlined.UploadFile, contentDescription = "Load Tape")
    }
}