package com.subbyte.subspectrum.base

import com.subbyte.subspectrum.proc.Processor
import subspectrum.composeapp.generated.resources.Res

data class SpectrumRom(
    val name: String,
    val path: String
) {
    companion object {
        val ROM48KB = SpectrumRom("48kB", "files/roms/48.rom")
        val ROMS = listOf(ROM48KB)
    }
}

object SpectrumMachine {
    suspend fun loadRom(path: String = SpectrumRom.ROM48KB.path) {
        reset()

        val romBytes = Res.readBytes(path)
        Memory.memorySet.setMemoryCells(0u, romBytes)
    }

    fun reset() {
        Processor.reset()

        Registers.normalRegisterSet.reset()
        Registers.shadowRegisterSet.reset()
        Registers.specialPurposeRegisters.reset()

        Memory.memorySet.reset()

        IO.ioPortSet.reset()
        ULATiming.reset()
        ULAKeyboard.releaseAllKeyboardKeys()

        ULATapeDeck.reset()
    }
}
