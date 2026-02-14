package com.subbyte.subspectrum.proc.instructions.jump

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayStringWithSign

data class DJNZd(
    override val address: Address,
    override val bytes: DataByteArray,
    val displacement: Byte
) : Instruction {
    private var jumpOccurred: Boolean = false

    override fun getTStates(): Int = if (jumpOccurred) 13 else 8

    override fun execute() {
        val bRegisterValue = Registers.registerSet.getB()
        val result = bRegisterValue.minus(1).toByte()
        Registers.registerSet.setB(result)

        jumpOccurred = result != 0.toByte()
        if (jumpOccurred) {
            val pcRegisterValue = Registers.specialPurposeRegisters.getPC()
            val newPC = pcRegisterValue.plus(displacement).toShort()
            Registers.specialPurposeRegisters.setPC(newPC)
        }
    }

    override fun toString(): String = "DJNZ ${displacement.displayStringWithSign()}"

    companion object Companion : InstructionDefinition {
        override val bitPattern = BitPattern.of("00010000 dddddddd")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val displacement = bitPattern.getByte(word, 'd')

            return DJNZd(address, bytes, displacement)
        }
    }
}
