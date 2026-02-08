package com.subbyte.subspectrum.proc.instructions.arith8

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray
data class ADCAHL(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 7

    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val hlRegisterPairValue = Registers.registerSet.getHL()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(hlRegisterPairValue.toUShort())
        val carryValue = if (Registers.registerSet.getCFlag()) 1 else 0

        val a = aRegisterValue.toUByte().toInt()
        val source = sourceMemoryValue.toUByte().toInt()
        val sum = a + source + carryValue
        val result = sum.toByte()

        Registers.registerSet.setA(result)

        val signFlag = result < 0
        val zeroFlag = result == 0.toByte()
        val halfCarryFlag = ((a and 0x0F) + (source and 0x0F) + carryValue) > 0x0F
        val overflowFlag = ((a xor sum) and (source xor sum) and 0x80) != 0
        val carryFlag = sum > 0xFF
        Registers.registerSet.setSFlag(signFlag)
        Registers.registerSet.setZFlag(zeroFlag)
        Registers.registerSet.setHFlag(halfCarryFlag)
        Registers.registerSet.setPVFlag(overflowFlag)
        Registers.registerSet.setNFlag(false)
        Registers.registerSet.setCFlag(carryFlag)
    }

    override fun toString(): String = "ADC A, (HL)"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("10001110")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return ADCAHL(address, bytes)
        }
    }
}
