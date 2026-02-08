package com.subbyte.subspectrum.proc.instructions.load16

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterPairSSCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.UWord
import com.subbyte.subspectrum.units.displayString

data class LDddnn(
    override val address: Address,
    override val bytes: DataByteArray,
    val destinationRegisterPair: RegisterPairSSCode,
    val sourceUWord: UWord
) : Instruction {
    override fun getTStates(): Int = 10

    override fun execute() {
        Registers.setRegisterPair(destinationRegisterPair, sourceUWord)
    }

    override fun toString(): String = "LD $destinationRegisterPair, ${sourceUWord.displayString()}"

    companion object Companion : InstructionDefinition {
        override val bitPattern = BitPattern.of("00dd0001 llllllll hhhhhhhh")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val destinationRegisterPair = bitPattern.getRegisterPairSSCode(word, 'd')
            val sourceUWord = bitPattern.getUWord(word, 'l', 'h')

            return LDddnn(address, bytes, destinationRegisterPair, sourceUWord)
        }
    }
}
