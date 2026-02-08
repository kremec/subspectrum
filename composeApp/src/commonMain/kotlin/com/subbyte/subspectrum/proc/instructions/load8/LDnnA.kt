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

data class LDnnA(
    override val address: Address,
    override val bytes: DataByteArray,
    val destinationUWord: UWord
) : Instruction {
    override fun getTStates(): Int = 13

    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        Memory.memorySet.setMemoryCell(destinationUWord, aRegisterValue)
    }

    override fun toString(): String = "LD (${destinationUWord.displayString()}), A"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("00110010 llllllll hhhhhhhh")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val destinationUWord = bitPattern.getUWord(word, 'l', 'h')

            return LDnnA(address, bytes, destinationUWord)
        }
    }
}
