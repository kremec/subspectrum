package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.units.getBit
import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class DECHL(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 11

    override fun execute() {
        val hlRegisterValue = Registers.registerSet.getHL()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(hlRegisterValue.toUShort())

        val source = sourceMemoryValue.toUByte().toInt()
        val diff = source - 1
        val result = diff.toByte()

        Memory.memorySet.setMemoryCell(hlRegisterValue.toUShort(), result)

        val signFlag = result < 0
        val zeroFlag = result == 0.toByte()
        val halfCarryFlag = (source and 0x0F) == 0x00
        val overflowFlag = source == 0x80
        Registers.registerSet.setSFlag(signFlag)
        Registers.registerSet.setZFlag(zeroFlag)
        Registers.registerSet.setYFFlag((result).getBit(5))
        Registers.registerSet.setXFFlag((result).getBit(3))
        Registers.registerSet.setHFlag(halfCarryFlag)
        Registers.registerSet.setPVFlag(overflowFlag)
        Registers.registerSet.setNFlag(true)
    }

    override fun toString(): String = "DEC (HL)"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("00110101")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return DECHL(address, bytes)
        }
    }
}
