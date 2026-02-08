package com.subbyte.subspectrum.proc.instructions.arith8

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayStringDisplacement

data class INCIXd(
    override val address: Address,
    override val bytes: DataByteArray,
    val displacement: Byte
) : Instruction {
    override fun getTStates(): Int = 23

    override fun execute() {
        val ixRegisterValue = Registers.specialPurposeRegisters.getIX()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(ixRegisterValue.plus(displacement).toUShort())

        val source = sourceMemoryValue.toUByte().toInt()
        val sum = source + 1
        val result = sum.toByte()

        Memory.memorySet.setMemoryCell(ixRegisterValue.plus(displacement).toUShort(), result)

        val signFlag = result < 0
        val zeroFlag = result == 0.toByte()
        val halfCarryFlag = (source and 0x0F) == 0x0F
        val overflowFlag = source == 0x7F
        Registers.registerSet.setSFlag(signFlag)
        Registers.registerSet.setZFlag(zeroFlag)
        Registers.registerSet.setHFlag(halfCarryFlag)
        Registers.registerSet.setPVFlag(overflowFlag)
        Registers.registerSet.setNFlag(false)
    }

    override fun toString(): String = "INC (IX${displacement.displayStringDisplacement()})"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11011101 00110100 dddddddd")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val displacement = bitPattern.getByte(word, 'd')

            return INCIXd(address, bytes, displacement)
        }
    }
}
