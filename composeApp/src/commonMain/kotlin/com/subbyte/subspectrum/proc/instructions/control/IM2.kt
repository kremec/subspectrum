package com.subbyte.subspectrum.proc.instructions.control

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class IM2(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 8

    override fun execute() {
        Processor.interruptMode = 2
    }

    override fun toString(): String = "IM 2"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 01011110")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return IM2(address, bytes)
        }
    }
}
