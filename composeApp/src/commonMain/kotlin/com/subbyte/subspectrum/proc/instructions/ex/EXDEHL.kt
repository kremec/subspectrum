package com.subbyte.subspectrum.proc.instructions.ex

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class EXDEHL(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 4

    override fun execute() {
        val deRegisterPairValue = Registers.registerSet.getDE()
        val hlRegisterPairValue = Registers.registerSet.getHL()

        Registers.registerSet.setDE(hlRegisterPairValue)
        Registers.registerSet.setHL(deRegisterPairValue)
    }

    override fun toString(): String = "EX DE, HL"


    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101011")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return EXDEHL(address, bytes)
        }
    }
}
