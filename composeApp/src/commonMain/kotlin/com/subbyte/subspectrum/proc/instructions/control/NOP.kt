package com.subbyte.subspectrum.proc.instructions.control

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class NOP(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 4

    override fun execute() {
        // NOP does nothing but consume 4 T-states
    }

    override fun toString(): String = "NOP"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("00000000")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return NOP(address, bytes)
        }
    }
}
