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
import com.subbyte.subspectrum.units.toBytes

data class LDnndd(
    override val address: Address,
    override val bytes: DataByteArray,
    val destinationUWord: UWord,
    val sourceRegisterPairCode: RegisterPairSSCode
) : Instruction {
    override fun getTStates(): Int = 20

    override fun execute() {
        val sourceValue = Registers.getRegisterPair(sourceRegisterPairCode)
        val (sourceLowValue, sourceHighValue) = sourceValue.toBytes()
        Memory.memorySet.setMemoryCells(destinationUWord, byteArrayOf(sourceLowValue, sourceHighValue))
    }

    override fun toString(): String = "LD (${destinationUWord.displayString()}), $sourceRegisterPairCode"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 01dd0011 llllllll hhhhhhhh")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val sourceRegisterPair = bitPattern.getRegisterPairSSCode(word, 'd')
            val destinationUWord = bitPattern.getUWord(word, 'l', 'h')

            return LDnndd(address, bytes, destinationUWord, sourceRegisterPair)
        }
    }
}
