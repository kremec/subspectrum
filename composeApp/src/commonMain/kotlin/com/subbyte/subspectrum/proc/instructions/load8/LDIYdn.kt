package com.subbyte.subspectrum.proc.instructions.load8

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayString
import com.subbyte.subspectrum.units.displayStringDisplacement

data class LDIYdn(
    override val address: Address,
    override val bytes: DataByteArray,
    val displacement: Byte,
    val sourceUByte: UByte
) : Instruction {
    override fun getTStates(): Int = 19

    override fun execute() {
        val iyRegisterValue = Registers.specialPurposeRegisters.getIY()
        Memory.memorySet.setMemoryCell(iyRegisterValue.plus(displacement).toUShort(), sourceUByte)
    }

    override fun toString(): String = "LD (IY${displacement.displayStringDisplacement()}), ${sourceUByte.displayString()}"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11111101 00110110 dddddddd nnnnnnnn")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val displacement = bitPattern.getByte(word, 'd')
            val sourceUByte = bitPattern.getUByte(word, 'n')

            return LDIYdn(address, bytes, displacement, sourceUByte)
        }
    }
}
