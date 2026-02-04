package com.subbyte.subspectrum.base

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

typealias IOPort = UByte
val IO_SIZE = IOPort.MAX_VALUE.toInt() + 1

data class IOPortSet (
    private val ioPorts: ByteArray = ByteArray(IO_SIZE)
) {
    private val _invalidations = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val invalidations: SharedFlow<Unit> = _invalidations.asSharedFlow()
    private fun invalidate() {
        _invalidations.tryEmit(Unit) // never suspends
    }

    fun getIOPort(port: IOPort): Byte {
        return ioPorts[port.toInt()]
    }

    fun setIOPort(port: IOPort, value: Byte) {
        ioPorts[port.toInt()] = value
        invalidate()
    }

    fun reset() {
        ioPorts.fill(0x00)
        invalidate()
    }
}

object IO {
    val ioPortSet: IOPortSet = IOPortSet()
}