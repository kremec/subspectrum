package com.subbyte.subspectrum.proc.instructions.load16

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterPairSSCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.UWord
import com.subbyte.subspectrum.units.displayString
import com.subbyte.subspectrum.units.wordFromBytes

data class LDddMEMnn(
    override val address: Address,
    override val bytes: DataByteArray,
    val destinationRegisterPair: RegisterPairSSCode,
    val sourceUWord: UWord
) : Instruction {
    override fun getTStates(): Int = 20

    override fun execute() {
        val sourceLowValue = Memory.memorySet.getMemoryCell(sourceUWord)
        val sourceHighValue = Memory.memorySet.getMemoryCell(sourceUWord.inc())
        val sourceValue = Pair(sourceHighValue, sourceLowValue).wordFromBytes()
        Registers.setRegisterPair(destinationRegisterPair, sourceValue)
    }

    override fun toString(): String = "LD $destinationRegisterPair, (${sourceUWord.displayString()})"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 01dd1011 llllllll hhhhhhhh")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val destinationRegisterPair = bitPattern.getRegisterPairSSCode(word, 'd')
            val sourceUWord = bitPattern.getUWord(word, 'l', 'h')

            return LDddMEMnn(address, bytes, destinationRegisterPair, sourceUWord)
        }
    }
}
