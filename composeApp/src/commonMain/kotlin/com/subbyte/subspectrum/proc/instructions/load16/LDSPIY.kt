package com.subbyte.subspectrum.proc.instructions.load16

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class LDSPIY(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 10

    override fun execute() {
        val sourceValue = Registers.specialPurposeRegisters.getIY()
        Registers.specialPurposeRegisters.setSP(sourceValue)
    }

    override fun toString(): String = "LD SP, IY"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11111101 11111001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return LDSPIY(address, bytes)
        }
    }
}
