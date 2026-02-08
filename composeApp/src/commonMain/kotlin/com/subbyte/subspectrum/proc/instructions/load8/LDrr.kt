package com.subbyte.subspectrum.proc.instructions.load8

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class LDrr(
    override val address: Address,
    override val bytes: DataByteArray,
    val destinationRegister: RegisterCode,
    val sourceRegister: RegisterCode
) : Instruction {
    override fun getTStates(): Int = 4

    override fun execute() {
        val sourceValue = Registers.registerSet.getRegister(sourceRegister)
        Registers.registerSet.setRegister(destinationRegister, sourceValue)
    }

    override fun toString(): String = "LD $destinationRegister, $sourceRegister"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("01xxxyyy")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val destinationRegister = bitPattern.getRegisterCode(word, 'x')
            val sourceRegister = bitPattern.getRegisterCode(word, 'y')

            return LDrr(address, bytes, destinationRegister, sourceRegister)
        }
    }
}
