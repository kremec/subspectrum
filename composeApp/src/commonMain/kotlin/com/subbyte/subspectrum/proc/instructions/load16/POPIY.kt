package com.subbyte.subspectrum.proc.instructions.load16

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class POPIY(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val sourceLowValue = Memory.memorySet.getMemoryCell(Registers.specialPurposeRegisters.getSP().toUShort())
        Registers.specialPurposeRegisters.setSP(Registers.specialPurposeRegisters.getSP().plus(1).toShort())
        val sourceHighValue = Memory.memorySet.getMemoryCell(Registers.specialPurposeRegisters.getSP().toUShort())
        Registers.specialPurposeRegisters.setSP(Registers.specialPurposeRegisters.getSP().plus(1).toShort())

        val sourceValue = ((sourceHighValue.toInt() shl 8) or (sourceLowValue.toInt() and 0xFF)).toShort()
        Registers.specialPurposeRegisters.setIY(sourceValue)
    }

    override fun toString(): String = "PUSH IY"

    companion object : InstructionDefinition {
        override val mCycles: Int = 4
        override val tStates: Int = 14

        override val bitPattern = BitPattern.of("11111101 11100001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return POPIY(address, bytes)
        }
    }
}