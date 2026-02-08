package com.subbyte.subspectrum.proc.instructions.shift

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayStringDisplacement
import com.subbyte.subspectrum.units.getBit

data class RLCIYd(
    override val address: Address,
    override val bytes: DataByteArray,
    val displacement: Byte
) : Instruction {
    override fun getTStates(): Int = 23

    override fun execute() {
        val iyRegisterPairValue = Registers.specialPurposeRegisters.getIY()
        val sourceValue = Memory.memorySet.getMemoryCell(iyRegisterPairValue.plus(displacement).toUShort())
        val carryValue = sourceValue.getBit(7)
        val result = sourceValue.rotateLeft(1)
        Memory.memorySet.setMemoryCell(iyRegisterPairValue.plus(displacement).toUShort(), result)

        Registers.registerSet.setSFlag(result < 0)
        Registers.registerSet.setZFlag(result == 0.toByte())
        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setPVFlag(result.countOneBits() % 2 == 0)
        Registers.registerSet.setNFlag(false)
        Registers.registerSet.setCFlag(carryValue)
    }

    override fun toString(): String = "RLC (IY${displacement.displayStringDisplacement()})"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11111101 11001011 dddddddd 00000110")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val displacement = bitPattern.getByte(word, 'd')

            return RLCIYd(address, bytes, displacement)
        }
    }
}
