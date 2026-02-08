package com.subbyte.subspectrum.proc.instructions.bit

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.setBit

data class SETbHL(
    override val address: Address,
    override val bytes: DataByteArray,
    val bitPosition: Int
) : Instruction {
    override fun getTStates(): Int = 15

    override fun execute() {
        val hlValue = Registers.registerSet.getHL()
        val memoryValue = Memory.memorySet.getMemoryCell(hlValue.toUShort())
        val newValue = memoryValue.setBit(bitPosition, true)
        Memory.memorySet.setMemoryCell(hlValue.toUShort(), newValue)
    }

    override fun toString(): String = "SET $bitPosition, (HL)"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11001011 11bbb110")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val bitPosition = bitPattern.getBitPosition(word, 'b')

            return SETbHL(address, bytes, bitPosition)
        }
    }
}
