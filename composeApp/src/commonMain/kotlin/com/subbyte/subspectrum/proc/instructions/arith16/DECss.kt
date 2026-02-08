package com.subbyte.subspectrum.proc.instructions.arith16

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterPairSSCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class DECss(
    override val address: Address,
    override val bytes: DataByteArray,
    val sourceRegisterPairCode: RegisterPairSSCode
) : Instruction {
    override fun getTStates(): Int = 6

    override fun execute() {
        val sourceValue = Registers.getRegisterPair(sourceRegisterPairCode)
        val result = sourceValue.dec()
        Registers.setRegisterPair(sourceRegisterPairCode, result)
    }

    override fun toString(): String = "DEC $sourceRegisterPairCode"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("00ss1011")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val sourceRegisterPair = bitPattern.getRegisterPairSSCode(word, 's')

            return DECss(address, bytes, sourceRegisterPair)
        }
    }
}
