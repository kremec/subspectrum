package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.Word

data class LDAnn(
    override val address: Address,
    override val bytes: ByteArray,
    val sourceWord: Word
) : Instruction {
    override fun execute() {
        val sourceValue = Memory.memorySet.getMemoryCell(sourceWord.toUShort())
        Registers.registerSet.setA(sourceValue)
    }

    override fun toString(): String = "LD A, (${sourceWord.toHexString(HexFormat.UpperCase)}h)"

    companion object : InstructionDefinition {
        override val mCycles: Int = 4
        override val tStates: Int = 13

        override val bitPattern = BitPattern.of("00111010 nnnnnnnn nnnnnnnn")
        override fun decode(word: Long, address: Address): Instruction {

            val n = bitPattern.get(word, 'n')

            val sourceWord = n.toShort()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return LDAnn(address, bytes, sourceWord)
        }
    }
}