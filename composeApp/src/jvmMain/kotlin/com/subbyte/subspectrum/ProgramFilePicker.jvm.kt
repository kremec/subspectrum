package com.subbyte.subspectrum

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import java.awt.FileDialog
import java.awt.Frame
import java.nio.file.Files
import java.nio.file.Paths

actual object ProgramFilePicker {
    actual suspend fun pickTapeProgramBytes(): ByteArray? = withContext(Dispatchers.Swing) {
        val fileDialog = FileDialog(null as Frame?, "Load Program", FileDialog.LOAD)
        fileDialog.setFilenameFilter { _, name ->
            name.endsWith(".tzx", ignoreCase = true) ||
                name.endsWith(".tap", ignoreCase = true)
        }
        fileDialog.isVisible = true

        val selectedDirectory = fileDialog.directory ?: return@withContext null
        val selectedFileName = fileDialog.file ?: return@withContext null
        val selectedPath = Paths.get(selectedDirectory, selectedFileName)

        val selectedFileNameLower = selectedPath.fileName.toString().lowercase()
        if (!selectedFileNameLower.endsWith(".tzx") && !selectedFileNameLower.endsWith(".tap")) {
            return@withContext null
        }

        Files.readAllBytes(selectedPath)
    }
}
