package com.subbyte.subspectrum.base

import androidx.compose.runtime.MutableState

internal expect object ULABeeper {
    val isMuted: MutableState<Boolean>

    fun toggleMuted()
    fun onPortWrite(portAddress: IOAddress, value: Byte)
    fun reset()
    fun shutdown()
}
