package com.subbyte.subspectrum.proc.instructions.jump

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class JPHL(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val hlRegisterPairValue = Registers.registerSet.getHL()
        Registers.specialPurposeRegisters.setPC(hlRegisterPairValue)
    }

    override fun toString(): String = "JP (HL)"

    companion object Companion : InstructionDefinition {
        override val mCycles: Int = 1
        override val tStates: Int = 4

        override val bitPattern = BitPattern.of("11101001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return JPHL(address, bytes)
        }
    }
}