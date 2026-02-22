package com.subbyte.subspectrum.proc.instructions.arith16

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class DECIY(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 10

    override fun execute() {
        val iyRegisterPairValue = Registers.specialPurposeRegisters.getIY()
        val result = iyRegisterPairValue.dec()
        Registers.specialPurposeRegisters.setIY(result)
    }

    override fun toString(): String = "DEC IY"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11111101 00101011")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return DECIY(address, bytes)
        }
    }
}
