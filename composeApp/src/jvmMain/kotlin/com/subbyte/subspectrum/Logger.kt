package com.subbyte.subspectrum

import java.io.BufferedWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption.APPEND
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import java.nio.file.StandardOpenOption.WRITE

actual object Logger {
    private val lock = Any()
    private val tracePath = Paths.get("pc-trace.log")
    private var writer: BufferedWriter? = null

    actual fun reset() {
        synchronized(lock) {
            writer?.close()
            writer = Files.newBufferedWriter(
                tracePath,
                WRITE,
                CREATE,
                TRUNCATE_EXISTING
            )
        }
    }

    actual fun appendLogLine(log: String) {
        synchronized(lock) {
            val currentWriter = writer ?: Files.newBufferedWriter(
                tracePath,
                WRITE,
                CREATE,
                APPEND
            ).also { writer = it }

            currentWriter.append(log)
            currentWriter.newLine()
            currentWriter.flush()
        }
    }
}
