package com.subbyte.subspectrum.proc.instructions.bit

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayStringDisplacement
import com.subbyte.subspectrum.units.getBit

data class BITbIYd(
    override val address: Address,
    override val bytes: DataByteArray,
    val bitPosition: Int,
    val displacement: Byte
) : Instruction {
    override fun getTStates(): Int = 20

    override fun execute() {
        val iyValue = Registers.specialPurposeRegisters.getIY()
        val sourceValue = Memory.memorySet.getMemoryCell(iyValue.plus(displacement).toUShort())
        val bitValue = sourceValue.getBit(bitPosition)

        Registers.registerSet.setZFlag(!bitValue)
        Registers.registerSet.setHFlag(true)
        Registers.registerSet.setNFlag(false)
        // S, P/V unknown
    }

    override fun toString(): String = "BIT $bitPosition, (IY${displacement.displayStringDisplacement()})"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11111101 11001011 dddddddd 01bbbrrr")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val bitPosition = bitPattern.getBitPosition(word, 'b')
            val displacement = bitPattern.getByte(word, 'd')

            return BITbIYd(address, bytes, bitPosition, displacement)
        }
    }
}
