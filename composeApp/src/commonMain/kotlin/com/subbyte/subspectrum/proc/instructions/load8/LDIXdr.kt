package com.subbyte.subspectrum.proc.instructions.load8

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayStringDisplacement

data class LDIXdr(
    override val address: Address,
    override val bytes: DataByteArray,
    val sourceRegister: RegisterCode,
    val displacement: Byte
) : Instruction {
    override fun getTStates(): Int = 19

    override fun execute() {
        val sourceValue = Registers.registerSet.getRegister(sourceRegister)
        val ixRegisterValue = Registers.specialPurposeRegisters.getIX()
        Memory.memorySet.setMemoryCell(ixRegisterValue.plus(displacement).toUShort(), sourceValue)
    }

    override fun toString(): String = "LD (IX${displacement.displayStringDisplacement()}), $sourceRegister"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11011101 01110rrr dddddddd")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val sourceRegister = bitPattern.getRegisterCode(word, 'r')
            val displacement = bitPattern.getByte(word, 'd')

            return LDIXdr(address, bytes, sourceRegister, displacement)
        }
    }
}
