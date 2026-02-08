package com.subbyte.subspectrum.proc.instructions.arith8

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayString

data class SBCAn(
    override val address: Address,
    override val bytes: DataByteArray,
    val sourceUByte: UByte
) : Instruction {
    override fun getTStates(): Int = 7

    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val carryValue = if (Registers.registerSet.getCFlag()) 1 else 0

        val a = aRegisterValue.toUByte().toInt()
        val source = sourceUByte.toInt()
        val diff = a - source - carryValue
        val result = diff.toByte()

        Registers.registerSet.setA(result)

        val signFlag = result < 0
        val zeroFlag = result == 0.toByte()
        val halfCarryFlag = ((a and 0x0F) - (source and 0x0F) - carryValue) < 0
        val overflowFlag = ((a xor source) and (a xor diff) and 0x80) != 0
        val carryFlag = diff < 0
        Registers.registerSet.setSFlag(signFlag)
        Registers.registerSet.setZFlag(zeroFlag)
        Registers.registerSet.setHFlag(halfCarryFlag)
        Registers.registerSet.setPVFlag(overflowFlag)
        Registers.registerSet.setNFlag(true)
        Registers.registerSet.setCFlag(carryFlag)
    }

    override fun toString(): String = "SBC A, ${sourceUByte.displayString()}"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11011110 nnnnnnnn")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val sourceUByte = bitPattern.getUByte(word, 'n')

            return SBCAn(address, bytes, sourceUByte)
        }
    }
}
