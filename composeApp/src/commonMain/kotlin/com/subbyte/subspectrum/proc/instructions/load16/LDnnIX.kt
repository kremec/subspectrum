package com.subbyte.subspectrum.proc.instructions.load16

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.Word
import com.subbyte.subspectrum.units.toBytes

data class LDnnIX(
    override val address: Address,
    override val bytes: ByteArray,
    val destinationWord: Word
) : Instruction {
    override fun execute() {
        val sourceValue = Registers.specialPurposeRegisters.getIX()
        val (sourceHighValue, sourceLowValue) = sourceValue.toBytes()
        Memory.memorySet.setMemoryCells(destinationWord.toUShort(), byteArrayOf(sourceLowValue, sourceHighValue))
    }

    override fun toString(): String = "LD (${destinationWord.toHexString(HexFormat.UpperCase)}h), IX"

    companion object : InstructionDefinition {
        override val mCycles: Int = 5
        override val tStates: Int = 16

        override val bitPattern = BitPattern.of("11011101 00100010 nnnnnnnn nnnnnnnn")
        override fun decode(word: Long, address: Address): Instruction {
            val n = bitPattern.get(word, 'n')

            val destinationWord = n.toShort()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return LDnnIX(address, bytes, destinationWord)
        }
    }
}