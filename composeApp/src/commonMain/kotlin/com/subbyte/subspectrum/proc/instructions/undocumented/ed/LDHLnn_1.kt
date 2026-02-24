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
import com.subbyte.subspectrum.units.wordFromBytes

data class LDHLnn_1(
    override val address: Address,
    override val bytes: DataByteArray,
    val sourceUWord: UWord
) : Instruction {
    override fun getTStates(): Int = 16 // TODO: Different timing than 0x2A opcode variant

    override fun execute() {
        val sourceLowValue = Memory.memorySet.getMemoryCell(sourceUWord)
        val sourceHighValue = Memory.memorySet.getMemoryCell(sourceUWord.inc())
        val sourceValue = Pair(sourceHighValue, sourceLowValue).wordFromBytes()
        Registers.registerSet.setHL(sourceValue)
    }

    override fun toString(): String = "LD HL, (${sourceUWord.displayString()})"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 01101011 llllllll hhhhhhhh")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val sourceUWord = bitPattern.getUWord(word, 'l', 'h')

            return LDHLnn_1(address, bytes, sourceUWord)
        }
    }
}
