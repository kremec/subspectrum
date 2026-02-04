package com.subbyte.subspectrum.proc.instructions.control

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class EI(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        Processor.IFF1 = true
        Processor.IFF2 = true
        Processor.afterEIDI = true
    }

    override fun toString(): String = "EI"

    companion object : InstructionDefinition {
        override val mCycles: Int = 1
        override val tStates: Int = 4

        override val bitPattern = BitPattern.of("11111011")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return EI(address, bytes)
        }
    }
}