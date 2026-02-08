package com.subbyte.subspectrum.proc.instructions.load16

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

data class LDnnIX(
    override val address: Address,
    override val bytes: DataByteArray,
    val destinationUWord: UWord
) : Instruction {
    override fun getTStates(): Int = 16

    override fun execute() {
        val sourceValue = Registers.specialPurposeRegisters.getIX()
        val (sourceLowValue, sourceHighValue) = sourceValue.toBytes()
        Memory.memorySet.setMemoryCells(destinationUWord, byteArrayOf(sourceLowValue, sourceHighValue))
    }

    override fun toString(): String = "LD (${destinationUWord.displayString()}), IX"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11011101 00100010 llllllll hhhhhhhh")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val destinationUWord = bitPattern.getUWord(word, 'l', 'h')

            return LDnnIX(address, bytes, destinationUWord)
        }
    }
}
