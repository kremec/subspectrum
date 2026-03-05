package com.subbyte.subspectrum.base

import com.subbyte.subspectrum.units.UWord
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

typealias IOAddress = UWord
typealias IOPort = UByte

fun IOAddress.toIOPort(): IOPort {
    return (this.toInt() and IOPort.MAX_VALUE.toInt()).toUByte()
}

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
        _invalidations.tryEmit(Unit)
    }

    fun getRawIO(portAddress: IOAddress): Byte {
        return ioPorts[portAddress.toIOPort().toInt()]
    }

    fun getIO(portAddress: IOAddress): Byte {
        val ulaPortValue = ULAKeyboard.getIOPortValue(portAddress)
        if (ulaPortValue != null) {
            return ulaPortValue
        }

        return getRawIO(portAddress)
    }

    fun setIO(portAddress: IOAddress, value: Byte) {
        ioPorts[portAddress.toIOPort().toInt()] = value
        ULABeeper.onPortWrite(portAddress, value)
        invalidate()
    }

    fun reset() {
        ioPorts.fill(0x00)
        ULABeeper.reset()
        invalidate()
    }
}

object IO {
    val ioPortSet: IOPortSet = IOPortSet()
}