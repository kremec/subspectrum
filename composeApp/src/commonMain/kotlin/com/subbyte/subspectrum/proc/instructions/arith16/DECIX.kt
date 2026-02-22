package com.subbyte.subspectrum.proc.instructions.arith16

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray
data class DECIX(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 10

    override fun execute() {
        val ixRegisterPairValue = Registers.specialPurposeRegisters.getIX()
        val result = ixRegisterPairValue.dec()
        Registers.specialPurposeRegisters.setIX(result)
    }

    override fun toString(): String = "DEC IX"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11011101 00101011")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return DECIX(address, bytes)
        }
    }
}
