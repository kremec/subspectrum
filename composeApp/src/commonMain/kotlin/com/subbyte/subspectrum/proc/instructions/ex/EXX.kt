package com.subbyte.subspectrum.proc.instructions.ex

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class EXX(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 4

    override fun execute() {
        val bcRegisterPairValue = Registers.normalRegisterSet.getBC()
        val bcShadowRegisterPairValue = Registers.shadowRegisterSet.getBC()
        val deRegisterPairValue = Registers.normalRegisterSet.getDE()
        val deShadowRegisterPairValue = Registers.shadowRegisterSet.getDE()
        val hlRegisterPairValue = Registers.normalRegisterSet.getHL()
        val hlShadowRegisterPairValue = Registers.shadowRegisterSet.getHL()

        Registers.normalRegisterSet.setBC(bcShadowRegisterPairValue)
        Registers.shadowRegisterSet.setBC(bcRegisterPairValue)
        Registers.normalRegisterSet.setDE(deShadowRegisterPairValue)
        Registers.shadowRegisterSet.setDE(deRegisterPairValue)
        Registers.normalRegisterSet.setHL(hlShadowRegisterPairValue)
        Registers.shadowRegisterSet.setHL(hlRegisterPairValue)
    }

    override fun toString(): String = "EXX'"


    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11011001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return EXX(address, bytes)
        }
    }
}
