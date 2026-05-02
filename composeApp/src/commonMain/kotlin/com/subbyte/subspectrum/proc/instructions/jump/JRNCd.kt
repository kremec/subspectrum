package com.subbyte.subspectrum.proc.instructions.jump

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayStringWithSign

data class JRNCd(
    override val address: Address,
    override val bytes: DataByteArray,
    val displacement: Byte
) : Instruction {
    private var conditionMet: Boolean = false

    override fun getTStates(): Int = if (conditionMet) 12 else 7

    override fun execute() {
        conditionMet = !Registers.registerSet.getCFlag()
        if (conditionMet) {
            val pcRegisterValue = Registers.specialPurposeRegisters.getPC()
            val newPC = pcRegisterValue.plus(displacement).toShort()
            Registers.specialPurposeRegisters.setMEMPTR(newPC)
            Registers.specialPurposeRegisters.setPC(newPC)
        }
    }

    override fun toString(): String = "JR NC, ${displacement.displayStringWithSign()}"

    companion object Companion : InstructionDefinition {
        override val bitPattern = BitPattern.of("00110000 dddddddd")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val displacement = bitPattern.getByte(word, 'd')

            return JRNCd(address, bytes, displacement)
        }
    }
}
