package com.subbyte.subspectrum

expect object ProgramFilePicker {
    suspend fun pickTzxProgramBytes(): ByteArray?
}
