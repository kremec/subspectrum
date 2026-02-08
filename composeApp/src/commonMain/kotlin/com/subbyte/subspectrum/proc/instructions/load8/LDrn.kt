package com.subbyte.subspectrum.proc.instructions.load8

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayString

data class LDrn(
    override val address: Address,
    override val bytes: DataByteArray,
    val destinationRegister: RegisterCode,
    val sourceUByte: UByte
) : Instruction {
    override fun getTStates(): Int = 7

    override fun execute() {
        Registers.registerSet.setRegister(destinationRegister, sourceUByte)
    }

    override fun toString(): String = "LD $destinationRegister, ${sourceUByte.displayString()}"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("00rrr110 nnnnnnnn")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val destinationRegister = bitPattern.getRegisterCode(word, 'r')
            val sourceUByte = bitPattern.getUByte(word, 'n')

            return LDrn(address, bytes, destinationRegister, sourceUByte)
        }
    }
}
