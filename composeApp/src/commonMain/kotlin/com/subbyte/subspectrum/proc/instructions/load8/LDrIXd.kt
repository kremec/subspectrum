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

data class LDrIXd(
    override val address: Address,
    override val bytes: DataByteArray,
    val destinationRegister: RegisterCode,
    val displacement: Byte
) : Instruction {
    override fun getTStates(): Int = 19

    override fun execute() {
        val ixRegisterValue = Registers.specialPurposeRegisters.getIX()
        val effectiveAddress = ixRegisterValue.plus(displacement)
        val sourceValue = Memory.memorySet.getMemoryCell(effectiveAddress.toUShort())
        Registers.specialPurposeRegisters.setMEMPTR(effectiveAddress.toShort())
        Registers.registerSet.setRegister(destinationRegister, sourceValue)
    }

    override fun toString(): String = "LD $destinationRegister, (IX${displacement.displayStringDisplacement()})"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11011101 01rrr110 dddddddd")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val destinationRegister = bitPattern.getRegisterCode(word, 'r')
            val displacement = bitPattern.getByte(word, 'd')

            return LDrIXd(address, bytes, destinationRegister, displacement)
        }
    }
}
