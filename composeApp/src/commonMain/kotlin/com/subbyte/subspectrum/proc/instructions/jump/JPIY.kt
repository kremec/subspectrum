package com.subbyte.subspectrum.proc.instructions.jump

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class JPIY(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 8

    override fun execute() {
        val iyValue = Registers.specialPurposeRegisters.getIY()
        Registers.specialPurposeRegisters.setPC(iyValue)
    }

    override fun toString(): String = "JP (IY)"


    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11111101 11101001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return JPIY(address, bytes)
        }
    }
}
