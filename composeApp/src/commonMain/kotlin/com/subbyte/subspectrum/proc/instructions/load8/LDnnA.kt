package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.Word

data class LDnnA(
    override val address: Address,
    override val bytes: ByteArray,
    val destinationWord: Word
) : Instruction {
    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        Memory.memorySet.setMemoryCell(destinationWord.toUShort(), aRegisterValue)
    }

    override fun toString(): String = "LD (${destinationWord.toHexString(HexFormat.UpperCase)}h), A"

    companion object : InstructionDefinition {
        override val mCycles: Int = 4
        override val tStates: Int = 13

        override val bitPattern = BitPattern.of("00110010 nnnnnnnn nnnnnnnn")
        override fun decode(word: Long, address: Address): Instruction {

            val n = bitPattern.get(word, 'n')

            val destinationWord = n.toShort()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return LDnnA(address, bytes, destinationWord)
        }
    }
}