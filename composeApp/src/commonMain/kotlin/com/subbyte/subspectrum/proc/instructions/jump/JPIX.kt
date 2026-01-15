package com.subbyte.subspectrum.proc.instructions.jump

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class JPIX(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val ixValue = Registers.specialPurposeRegisters.getIX()
        Registers.specialPurposeRegisters.setPC(ixValue)
    }

    override fun toString(): String = "JP (IX)"

    companion object : InstructionDefinition {
        override val mCycles: Int = 2
        override val tStates: Int = 8

        override val bitPattern = BitPattern.of("1101110111101001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return JPIX(address, bytes)
        }
    }
}