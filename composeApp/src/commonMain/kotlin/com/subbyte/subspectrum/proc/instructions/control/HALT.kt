package com.subbyte.subspectrum.proc.instructions.control

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class HALT(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 4

    override fun execute() {
        val pcRegisterValue = Registers.specialPurposeRegisters.getPC()
        Registers.specialPurposeRegisters.setPC(pcRegisterValue.minus(bitPattern.byteCount).toShort())

        Processor.inHalt = true
    }

    override fun toString(): String = "HALT"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("01110110")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return HALT(address, bytes)
        }
    }
}
