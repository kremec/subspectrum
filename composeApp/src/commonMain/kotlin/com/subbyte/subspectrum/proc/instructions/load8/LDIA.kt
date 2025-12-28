package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class LDIA(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        Registers.specialPurposeRegisters.setI(aRegisterValue)
    }

    override fun toString(): String = "LD I, A"

    companion object : InstructionDefinition {
        override val mCycles: Int = 2
        override val tStates: Int = 9

        override val bitPattern = BitPattern.of("11101101 01000111")
        override fun decode(word: Long, address: Address): Instruction {

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return LDIA(address, bytes)
        }
    }
}