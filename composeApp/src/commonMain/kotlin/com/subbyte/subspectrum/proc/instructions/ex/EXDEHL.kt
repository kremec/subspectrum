package com.subbyte.subspectrum.proc.instructions.ex

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class EXDEHL(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val deRegisterPairValue = Registers.registerSet.getDE()
        val hlRegisterPairValue = Registers.registerSet.getHL()

        Registers.registerSet.setDE(hlRegisterPairValue)
        Registers.registerSet.setHL(deRegisterPairValue)
    }

    override fun toString(): String = "EX DE, HL"

    companion object : InstructionDefinition {
        override val mCycles: Int = 1
        override val tStates: Int = 4

        override val bitPattern = BitPattern.of("11101011")
        override fun decode(word: Long, address: Address): Instruction {

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return EXDEHL(address, bytes)
        }
    }
}