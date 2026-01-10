package com.subbyte.subspectrum.proc.instructions.arith16

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class DECIX(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val ixRegisterPairValue = Registers.specialPurposeRegisters.getIX()
        val result = ixRegisterPairValue.dec()
        Registers.specialPurposeRegisters.setIX(result)
    }

    override fun toString(): String = "DEC IX"

    companion object : InstructionDefinition {
        override val mCycles: Int = 2
        override val tStates: Int = 10

        override val bitPattern = BitPattern.of("110111101 00101011")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return DECIX(address, bytes)
        }
    }
}