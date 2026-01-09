package com.subbyte.subspectrum.proc.instructions.ex

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class EXAFAF(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val afRegisterPairValue = Registers.normalRegisterSet.getAF()
        val afShadowRegisterPairValue = Registers.shadowRegisterSet.getAF()

        Registers.normalRegisterSet.setAF(afShadowRegisterPairValue)
        Registers.shadowRegisterSet.setAF(afRegisterPairValue)
    }

    override fun toString(): String = "EX AF, AF'"

    companion object : InstructionDefinition {
        override val mCycles: Int = 1
        override val tStates: Int = 4

        override val bitPattern = BitPattern.of("00001000")
        override fun decode(word: Long, address: Address): Instruction {

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return EXAFAF(address, bytes)
        }
    }
}