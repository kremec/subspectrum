package com.subbyte.subspectrum.proc.instructions.control

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class SCF(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 4

    override fun execute() {
        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setNFlag(false)
        Registers.registerSet.setCFlag(true)
    }

    override fun toString(): String = "SCF"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("00110111")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return SCF(address, bytes)
        }
    }
}
