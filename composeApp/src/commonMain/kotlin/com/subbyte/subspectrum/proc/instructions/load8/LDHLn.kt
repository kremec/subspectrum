package com.subbyte.subspectrum.proc.instructions.load8

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayString

data class LDHLn(
    override val address: Address,
    override val bytes: DataByteArray,
    val sourceUByte: UByte
) : Instruction {
    override fun getTStates(): Int = 10

    override fun execute() {
        val hlRegisterPairValue = Registers.registerSet.getHL()
        Memory.memorySet.setMemoryCell(hlRegisterPairValue.toUShort(), sourceUByte)
    }

    override fun toString(): String = "LD (HL), ${sourceUByte.displayString()}"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("00110110 nnnnnnnn")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val sourceUByte = bitPattern.getUByte(word, 'n')

            return LDHLn(address, bytes, sourceUByte)
        }
    }
}
