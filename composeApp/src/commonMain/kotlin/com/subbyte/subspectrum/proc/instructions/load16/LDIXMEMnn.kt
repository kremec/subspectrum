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
import com.subbyte.subspectrum.units.wordFromBytes

data class LDIXMEMnn(
    override val address: Address,
    override val bytes: DataByteArray,
    val sourceUWord: UWord
) : Instruction {
    override fun getTStates(): Int = 20

    override fun execute() {
        val sourceLowValue = Memory.memorySet.getMemoryCell(sourceUWord)
        val sourceHighValue = Memory.memorySet.getMemoryCell(sourceUWord.inc())
        val sourceValue = Pair(sourceHighValue, sourceLowValue).wordFromBytes()
        Registers.specialPurposeRegisters.setIX(sourceValue)
    }

    override fun toString(): String = "LD IX, (${sourceUWord.displayString()})"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11011101 00101010 llllllll hhhhhhhh")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val sourceUWord = bitPattern.getUWord(word, 'l', 'h')

            return LDIXMEMnn(address, bytes, sourceUWord)
        }
    }
}
