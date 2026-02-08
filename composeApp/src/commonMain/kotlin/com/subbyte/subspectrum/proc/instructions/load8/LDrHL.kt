package com.subbyte.subspectrum.proc.instructions.load8

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class LDrHL(
    override val address: Address,
    override val bytes: DataByteArray,
    val destinationRegister: RegisterCode
) : Instruction {
    override fun getTStates(): Int = 7

    override fun execute() {
        val hlRegisterPairValue = Registers.registerSet.getHL()
        val sourceValue = Memory.memorySet.getMemoryCell(hlRegisterPairValue.toUShort())
        Registers.registerSet.setRegister(destinationRegister, sourceValue)
    }

    override fun toString(): String = "LD $destinationRegister, (HL)"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("01rrr110")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val destinationRegister = bitPattern.getRegisterCode(word, 'r')

            return LDrHL(address, bytes, destinationRegister)
        }
    }
}
