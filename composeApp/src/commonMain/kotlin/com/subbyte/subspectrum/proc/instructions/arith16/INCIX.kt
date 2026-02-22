package com.subbyte.subspectrum.proc.instructions.arith16

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class INCIX(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 10

    override fun execute() {
        val ixRegisterPairValue = Registers.specialPurposeRegisters.getIX()
        val result = ixRegisterPairValue.inc()
        Registers.specialPurposeRegisters.setIX(result)
    }

    override fun toString(): String = "INC IX"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11011101 00100011")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return INCIX(address, bytes)
        }
    }
}
