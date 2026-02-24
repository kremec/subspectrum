package com.subbyte.subspectrum.proc.instructions.undocumented.ed

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.UWord
import com.subbyte.subspectrum.units.displayString
import com.subbyte.subspectrum.units.toBytes

data class LDnnHL_1(
    override val address: Address,
    override val bytes: DataByteArray,
    val destinationUWord: UWord
) : Instruction {
    override fun getTStates(): Int = 16 // TODO: Different timing than 0x22 opcode variant

    override fun execute() {
        val sourceValue = Registers.registerSet.getHL()
        val (sourceHighValue, sourceLowValue) = sourceValue.toBytes()
        Memory.memorySet.setMemoryCells(destinationUWord, byteArrayOf(sourceLowValue, sourceHighValue))
    }

    override fun toString(): String = "LD (${destinationUWord.displayString()}), HL"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 01100011 llllllll hhhhhhhh")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val destinationUWord = bitPattern.getUWord(word, 'l', 'h')

            return LDnnHL_1(address, bytes, destinationUWord)
        }
    }
}
