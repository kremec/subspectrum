package com.subbyte.subspectrum.proc.instructions.load8

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class LDRA(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 9

    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        Registers.specialPurposeRegisters.setR(aRegisterValue)
    }

    override fun toString(): String = "LD R, A"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 01001111")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return LDRA(address, bytes)
        }
    }
}
