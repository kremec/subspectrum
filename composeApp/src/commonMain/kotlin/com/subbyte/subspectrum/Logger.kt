package com.subbyte.subspectrum

expect object Logger {
    fun reset()
    fun appendLogLine(log: String)
}
