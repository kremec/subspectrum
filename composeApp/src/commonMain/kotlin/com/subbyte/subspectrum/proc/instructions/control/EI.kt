package com.subbyte.subspectrum.proc.instructions.control

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray
data class EI(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 4

    override fun execute() {
        Processor.IFF1 = true
        Processor.IFF2 = true
        Processor.afterEIDI = true
    }

    override fun toString(): String = "EI"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11111011")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return EI(address, bytes)
        }
    }
}
