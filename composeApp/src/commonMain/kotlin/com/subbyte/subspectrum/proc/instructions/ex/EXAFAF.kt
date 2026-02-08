package com.subbyte.subspectrum.proc.instructions.ex

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class EXAFAF(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 4

    override fun execute() {
        val afRegisterPairValue = Registers.normalRegisterSet.getAF()
        val afShadowRegisterPairValue = Registers.shadowRegisterSet.getAF()

        Registers.normalRegisterSet.setAF(afShadowRegisterPairValue)
        Registers.shadowRegisterSet.setAF(afRegisterPairValue)
    }

    override fun toString(): String = "EX AF, AF'"


    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("00001000")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return EXAFAF(address, bytes)
        }
    }
}
