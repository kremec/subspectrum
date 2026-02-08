package com.subbyte.subspectrum.proc.instructions.jump

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class JPHL(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 4

    override fun execute() {
        val hlRegisterPairValue = Registers.registerSet.getHL()
        Registers.specialPurposeRegisters.setPC(hlRegisterPairValue)
    }

    override fun toString(): String = "JP (HL)"


    companion object Companion : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return JPHL(address, bytes)
        }
    }
}
