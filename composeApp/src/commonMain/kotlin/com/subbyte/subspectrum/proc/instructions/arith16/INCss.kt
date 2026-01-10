package com.subbyte.subspectrum.proc.instructions.arith16

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterPairCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class INCss(
    override val address: Address,
    override val bytes: ByteArray,
    val sourceRegisterPairCode: RegisterPairCode
) : Instruction {
    override fun execute() {
        val sourceValue = Registers.getRegisterPair(sourceRegisterPairCode)
        val result = sourceValue.inc()
        Registers.setRegisterPair(sourceRegisterPairCode, result)
    }

    override fun toString(): String = "INC $sourceRegisterPairCode"

    companion object : InstructionDefinition {
        override val mCycles: Int = 1
        override val tStates: Int = 6

        override val bitPattern = BitPattern.of("00ss0011")
        override fun decode(word: Long, address: Address): Instruction {
            val s = bitPattern.get(word, 's')

            val sourceRegisterPair = RegisterPairCode.entries.first { it.code == s }

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return INCss(address, bytes, sourceRegisterPair)
        }
    }
}