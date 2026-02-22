package com.subbyte.subspectrum.proc.instructions.jump

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class JPIX(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 8

    override fun execute() {
        val ixValue = Registers.specialPurposeRegisters.getIX()
        Registers.specialPurposeRegisters.setPC(ixValue)
    }

    override fun toString(): String = "JP (IX)"


    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11011101 11101001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return JPIX(address, bytes)
        }
    }
}
