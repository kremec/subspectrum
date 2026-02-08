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

data class RESbIYd(
    override val address: Address,
    override val bytes: DataByteArray,
    val bitPosition: Int,
    val displacement: Byte
) : Instruction {
    override fun getTStates(): Int = 20

    override fun execute() {
        val iyValue = Registers.specialPurposeRegisters.getIY()
        val targetAddress = iyValue.plus(displacement).toUShort()
        val memoryValue = Memory.memorySet.getMemoryCell(targetAddress)
        val newValue = memoryValue.setBit(bitPosition, false)
        Memory.memorySet.setMemoryCell(targetAddress, newValue)
    }

    override fun toString(): String = "RES $bitPosition, (IY${displacement.displayStringDisplacement()})"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11111101 11001011 dddddddd 10bbb110")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val bitPosition = bitPattern.getBitPosition(word, 'b')
            val displacement = bitPattern.getByte(word, 'd')

            return RESbIYd(address, bytes, bitPosition, displacement)
        }
    }
}
