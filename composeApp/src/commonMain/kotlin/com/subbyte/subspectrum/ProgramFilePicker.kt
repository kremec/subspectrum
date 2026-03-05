package com.subbyte.subspectrum

expect object ProgramFilePicker {
    suspend fun pickTapeProgramBytes(): ByteArray?
}
