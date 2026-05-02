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
import com.subbyte.subspectrum.units.toBytes

data class BITbIXd(
    override val address: Address,
    override val bytes: DataByteArray,
    val bitPosition: Int,
    val displacement: Byte
) : Instruction {
    override fun getTStates(): Int = 20

    override fun execute() {
        val ixValue = Registers.specialPurposeRegisters.getIX()
        val effectiveAddress = ixValue.plus(displacement)
        val sourceValue = Memory.memorySet.getMemoryCell(effectiveAddress.toUShort())
        val bitValue = sourceValue.getBit(bitPosition)
        val effectiveAddressHigh = effectiveAddress.toShort().toBytes().first

        Registers.registerSet.setSFlag(bitPosition == 7 && bitValue)
        Registers.registerSet.setZFlag(!bitValue)
        Registers.registerSet.setYFFlag(effectiveAddressHigh.getBit(5))
        Registers.registerSet.setXFFlag(effectiveAddressHigh.getBit(3))
        Registers.registerSet.setHFlag(true)
        Registers.registerSet.setPVFlag(!bitValue)
        Registers.registerSet.setNFlag(false)
    }

    override fun toString(): String = "BIT $bitPosition, (IX${displacement.displayStringDisplacement()})"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11011101 11001011 dddddddd 01bbbrrr")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val bitPosition = bitPattern.getBitPosition(word, 'b')
            val displacement = bitPattern.getByte(word, 'd')

            return BITbIXd(address, bytes, bitPosition, displacement)
        }
    }
}
