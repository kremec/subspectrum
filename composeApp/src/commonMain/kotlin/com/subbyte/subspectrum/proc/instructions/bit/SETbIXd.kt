package com.subbyte.subspectrum.proc.instructions.bit

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayStringDisplacement
import com.subbyte.subspectrum.units.setBit

data class SETbIXd(
    override val address: Address,
    override val bytes: DataByteArray,
    val bitPosition: Int,
    val displacement: Byte
) : Instruction {
    override fun getTStates(): Int = 23

    override fun execute() {
        val ixValue = Registers.specialPurposeRegisters.getIX()
        val targetAddress = ixValue.plus(displacement).toUShort()
        val memoryValue = Memory.memorySet.getMemoryCell(targetAddress)
        val newValue = memoryValue.setBit(bitPosition, true)
        Memory.memorySet.setMemoryCell(targetAddress, newValue)
    }

    override fun toString(): String = "SET $bitPosition, (IX${displacement.displayStringDisplacement()})"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11011101 11001011 dddddddd 11bbb110")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val bitPosition = bitPattern.getBitPosition(word, 'b')
            val displacement = bitPattern.getByte(word, 'd')

            return SETbIXd(address, bytes, bitPosition, displacement)
        }
    }
}
