package com.subbyte.subspectrum.proc.instructions.undocumented.ed

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class IM0_2(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 8

    override fun execute() {
        Processor.interruptMode = 0
    }

    override fun toString(): String = "IM 0"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 01100110")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return IM0_2(address, bytes)
        }
    }
}
