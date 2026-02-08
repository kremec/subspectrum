package com.subbyte.subspectrum.proc.instructions.load8

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.UWord
import com.subbyte.subspectrum.units.displayString

data class LDAnn(
    override val address: Address,
    override val bytes: DataByteArray,
    val sourceUWord: UWord
) : Instruction {
    override fun getTStates(): Int = 13

    override fun execute() {
        val sourceValue = Memory.memorySet.getMemoryCell(sourceUWord)
        Registers.registerSet.setA(sourceValue)
    }

    override fun toString(): String = "LD A, (${sourceUWord.displayString()})"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("00111010 llllllll hhhhhhhh")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val sourceUWord = bitPattern.getUWord(word, 'l', 'h')

            return LDAnn(address, bytes, sourceUWord)
        }
    }
}
