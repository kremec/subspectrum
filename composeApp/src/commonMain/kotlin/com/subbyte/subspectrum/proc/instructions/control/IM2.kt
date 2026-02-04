package com.subbyte.subspectrum.proc.instructions.control

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class IM2(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        Processor.interruptMode = 2
    }

    override fun toString(): String = "IM 2"

    companion object : InstructionDefinition {
        override val mCycles: Int = 2
        override val tStates: Int = 8

        override val bitPattern = BitPattern.of("11101101 01011110")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return IM2(address, bytes)
        }
    }
}