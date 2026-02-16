package com.subbyte.subspectrum

actual object Logger {
    actual fun reset() = Unit

    actual fun appendLogLine(log: String) = Unit
}
