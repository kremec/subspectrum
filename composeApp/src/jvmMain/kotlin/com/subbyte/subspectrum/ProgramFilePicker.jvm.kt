package com.subbyte.subspectrum

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import java.awt.FileDialog
import java.awt.Frame
import java.nio.file.Files
import java.nio.file.Paths

actual object ProgramFilePicker {
    actual suspend fun pickTzxProgramBytes(): ByteArray? = withContext(Dispatchers.Swing) {
        val fileDialog = FileDialog(null as Frame?, "Load Program", FileDialog.LOAD)
        fileDialog.setFilenameFilter { _, name ->
            name.endsWith(".tzx", ignoreCase = true)
        }
        fileDialog.isVisible = true

        val selectedDirectory = fileDialog.directory ?: return@withContext null
        val selectedFileName = fileDialog.file ?: return@withContext null
        val selectedPath = Paths.get(selectedDirectory, selectedFileName)

        if (!selectedPath.fileName.toString().endsWith(".tzx", ignoreCase = true)) {
            return@withContext null
        }

        Files.readAllBytes(selectedPath)
    }
}
