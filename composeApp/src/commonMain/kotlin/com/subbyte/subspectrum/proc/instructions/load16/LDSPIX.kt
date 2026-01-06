package com.subbyte.subspectrum.proc.instructions.load16

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterPairCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.Word
import com.subbyte.subspectrum.units.toBytes

data class LDSPIX(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val sourceValue = Registers.specialPurposeRegisters.getIX()
        Registers.specialPurposeRegisters.setSP(sourceValue)
    }

    override fun toString(): String = "LD SP, IX"

    companion object : InstructionDefinition {
        override val mCycles: Int = 2
        override val tStates: Int = 10

        override val bitPattern = BitPattern.of("11011101 11111001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return LDSPIX(address, bytes)
        }
    }
}