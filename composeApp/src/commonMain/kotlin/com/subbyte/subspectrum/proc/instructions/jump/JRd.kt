package com.subbyte.subspectrum.proc.instructions.jump

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayStringWithSign

data class JRd(
    override val address: Address,
    override val bytes: DataByteArray,
    val displacement: Byte
) : Instruction {
    override fun getTStates(): Int = 12

    override fun execute() {
        val pcRegisterValue = Registers.specialPurposeRegisters.getPC()
        val newPC = pcRegisterValue.plus(displacement).toShort()
        Registers.specialPurposeRegisters.setPC(newPC)
    }

    override fun toString(): String = "JR ${displacement.displayStringWithSign()}"

    companion object Companion : InstructionDefinition {
        override val bitPattern = BitPattern.of("00011000 dddddddd")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val displacement = bitPattern.getByte(word, 'd')

            return JRd(address, bytes, displacement)
        }
    }
}
