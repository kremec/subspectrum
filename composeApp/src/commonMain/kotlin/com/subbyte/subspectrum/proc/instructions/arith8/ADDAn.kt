package com.subbyte.subspectrum.proc.instructions.arith8

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayString

data class ADDAn(
    override val address: Address,
    override val bytes: DataByteArray,
    val sourceUByte: UByte
) : Instruction {
    override fun getTStates(): Int = 7

    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()

        val a = aRegisterValue.toUByte().toInt()
        val source = sourceUByte.toInt()
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

    override fun toString(): String = "ADD A, ${sourceUByte.displayString()}"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11000110 nnnnnnnn")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val sourceUByte = bitPattern.getUByte(word, 'n')

            return ADDAn(address, bytes, sourceUByte)
        }
    }
}
