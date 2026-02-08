package com.subbyte.subspectrum.proc.instructions.arith8

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayStringDisplacement

data class ADDAIXd(
    override val address: Address,
    override val bytes: DataByteArray,
    val displacement: Byte
) : Instruction {
    override fun getTStates(): Int = 19

    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val ixRegisterPairValue = Registers.specialPurposeRegisters.getIX()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(ixRegisterPairValue.plus(displacement).toUShort())

        val a = aRegisterValue.toUByte().toInt()
        val source = sourceMemoryValue.toUByte().toInt()
        val sum = a + source
        val result = sum.toByte()

        Registers.registerSet.setA(result)

        val signFlag = result < 0
        val zeroFlag = result == 0.toByte()
        val halfCarryFlag = ((a and 0x0F) + (source and 0x0F)) > 0x0F
        val overflowFlag = ((a xor sum) and (source xor sum) and 0x80) != 0
        val carryFlag = sum > 0xFF
        Registers.registerSet.setSFlag(signFlag)
        Registers.registerSet.setZFlag(zeroFlag)
        Registers.registerSet.setHFlag(halfCarryFlag)
        Registers.registerSet.setPVFlag(overflowFlag)
        Registers.registerSet.setNFlag(false)
        Registers.registerSet.setCFlag(carryFlag)
    }

    override fun toString(): String = "ADD A, (IX${displacement.displayStringDisplacement()})"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11011101 10000110 dddddddd")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val displacement = bitPattern.getByte(word, 'd')

            return ADDAIXd(address, bytes, displacement)
        }
    }
}
