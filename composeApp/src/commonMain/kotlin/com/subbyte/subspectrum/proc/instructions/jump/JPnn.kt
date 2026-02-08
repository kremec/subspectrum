package com.subbyte.subspectrum.proc.instructions.jump

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayString

data class JPnn(
    override val address: Address,
    override val bytes: DataByteArray,
    val targetAddress: Address
) : Instruction {
    override fun getTStates(): Int = 10

    override fun execute() {
        Registers.specialPurposeRegisters.setPC(targetAddress.toShort())
    }

    override fun toString(): String = "JP ${targetAddress.displayString()}"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11000011 llllllll hhhhhhhh")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val targetAddress = bitPattern.getUWord(word, 'l', 'h')

            return JPnn(address, bytes, targetAddress)
        }
    }
}
