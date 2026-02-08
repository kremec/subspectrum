package com.subbyte.subspectrum.proc.instructions.control

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray
data class DI(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 4

    override fun execute() {
        Processor.IFF1 = false
        Processor.IFF2 = false
        Processor.afterEIDI = true
    }

    override fun toString(): String = "DI"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11110011")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return DI(address, bytes)
        }
    }
}
