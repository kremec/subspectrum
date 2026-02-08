package com.subbyte.subspectrum.proc.instructions.load16

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class LDSPHL(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 6
    override fun execute() {
        val sourceValue = Registers.registerSet.getHL()
        Registers.specialPurposeRegisters.setSP(sourceValue)
    }

    override fun toString(): String = "LD SP, HL"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11111001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return LDSPHL(address, bytes)
        }
    }
}
