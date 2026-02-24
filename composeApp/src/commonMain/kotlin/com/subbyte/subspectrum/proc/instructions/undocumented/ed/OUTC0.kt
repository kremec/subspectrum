package com.subbyte.subspectrum.proc.instructions.undocumented.ed

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray

data class OUTC0(
    override val address: Address,
    override val bytes: DataByteArray,
) : Instruction {
    override fun getTStates(): Int = 12

    override fun execute() {
        val destinationIOPortAddress = Registers.registerSet.getBC().toUShort()
        IO.ioPortSet.setIO(destinationIOPortAddress, 0x00)
    }

    override fun toString(): String = "OUT (C), 0"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 01110001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return OUTC0(address, bytes)
        }
    }
}
