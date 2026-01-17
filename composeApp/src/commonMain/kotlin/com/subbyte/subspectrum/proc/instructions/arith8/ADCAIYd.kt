package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class ADCAIYd(
    override val address: Address,
    override val bytes: ByteArray,
    val displacement: Byte
) : Instruction {
    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val iyRegisterPairValue = Registers.specialPurposeRegisters.getIY()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(iyRegisterPairValue.plus(displacement).toUShort())
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

    override fun toString(): String = "ADC A, (IY + ${displacement})"

    companion object : InstructionDefinition {
        override val mCycles: Int = 5
        override val tStates: Int = 19

        override val bitPattern = BitPattern.of("11111101 10001110 dddddddd")
        override fun decode(word: Long, address: Address): Instruction {
            val d = bitPattern.get(word, 'd')

            val displacement = d.toByte()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return ADCAIYd(address, bytes, displacement)
        }
    }
}