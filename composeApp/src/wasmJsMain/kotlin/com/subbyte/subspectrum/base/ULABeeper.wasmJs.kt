package com.subbyte.subspectrum.base

import androidx.compose.runtime.mutableStateOf

internal actual object ULABeeper {
    actual val isMuted = mutableStateOf(false)

    actual fun toggleMuted() {
        isMuted.value = !isMuted.value
    }

    actual fun onPortWrite(portAddress: IOAddress, value: Byte) = Unit

    actual fun reset() = Unit

    actual fun shutdown() = Unit
}
