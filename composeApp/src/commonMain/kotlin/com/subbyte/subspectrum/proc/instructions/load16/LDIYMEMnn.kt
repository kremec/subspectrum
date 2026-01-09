package com.subbyte.subspectrum.proc.instructions.load16

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.Word
import com.subbyte.subspectrum.units.fromBytes

data class LDIYMEMnn(
    override val address: Address,
    override val bytes: ByteArray,
    val sourceWord: Word
) : Instruction {
    override fun execute() {
        val sourceLowValue = Memory.memorySet.getMemoryCell(sourceWord.toUShort())
        val sourceHighValue = Memory.memorySet.getMemoryCell(sourceWord.inc().toUShort())
        val sourceValue = ((sourceHighValue.toInt() shl 8) or (sourceLowValue.toInt() and 0xFF)).toShort()
        Registers.specialPurposeRegisters.setIY(sourceValue)
    }

    override fun toString(): String = "LD IY, (${sourceWord.toHexString(HexFormat.UpperCase)}h)"

    companion object : InstructionDefinition {
        override val mCycles: Int = 6
        override val tStates: Int = 20

        override val bitPattern = BitPattern.of("11011101 00101010 llllllll hhhhhhhh")
        override fun decode(word: Long, address: Address): Instruction {
            val l = bitPattern.get(word, 'l').toByte()
            val h = bitPattern.get(word, 'h').toByte()

            val sourceWord = Pair(h, l).fromBytes()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return LDIYMEMnn(address, bytes, sourceWord)
        }
    }
}