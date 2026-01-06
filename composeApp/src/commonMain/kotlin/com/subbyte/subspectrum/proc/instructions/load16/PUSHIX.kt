package com.subbyte.subspectrum.proc.instructions.load16

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.toBytes

data class PUSHIX(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val sourceValue = Registers.specialPurposeRegisters.getIX()
        val (sourceHighValue, sourceLowValue) = sourceValue.toBytes()

        Registers.specialPurposeRegisters.setSP(Registers.specialPurposeRegisters.getSP().minus(1).toShort())
        Memory.memorySet.setMemoryCell(Registers.specialPurposeRegisters.getSP().toUShort(), sourceHighValue)
        Registers.specialPurposeRegisters.setSP(Registers.specialPurposeRegisters.getSP().minus(1).toShort())
        Memory.memorySet.setMemoryCell(Registers.specialPurposeRegisters.getSP().toUShort(), sourceLowValue)
    }

    override fun toString(): String = "PUSH IX"

    companion object : InstructionDefinition {
        override val mCycles: Int = 4
        override val tStates: Int = 15

        override val bitPattern = BitPattern.of("11011101 11100101")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return PUSHIX(address, bytes)
        }
    }
}