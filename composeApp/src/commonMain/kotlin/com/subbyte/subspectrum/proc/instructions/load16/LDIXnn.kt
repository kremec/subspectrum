package com.subbyte.subspectrum.proc.instructions.load16

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.UWord
import com.subbyte.subspectrum.units.displayString

data class LDIXnn(
    override val address: Address,
    override val bytes: DataByteArray,
    val sourceUWord: UWord
) : Instruction {
    override fun getTStates(): Int = 14

    override fun execute() {
        Registers.specialPurposeRegisters.setIX(sourceUWord)
    }

    override fun toString(): String = "LD IX, ${sourceUWord.displayString()}"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11011101 00100001 llllllll hhhhhhhh")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val sourceUWord = bitPattern.getUWord(word, 'l', 'h')

            return LDIXnn(address, bytes, sourceUWord)
        }
    }
}
