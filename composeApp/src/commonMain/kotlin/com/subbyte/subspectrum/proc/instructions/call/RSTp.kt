package com.subbyte.subspectrum.proc.instructions.call

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayString
import com.subbyte.subspectrum.units.toBytes

data class RSTp(
    override val address: Address,
    override val bytes: DataByteArray,
    val restartAddress: UByte
) : Instruction {
    override fun getTStates(): Int = 11

    override fun execute() {
        val pcRegisterPairValue = Registers.specialPurposeRegisters.getPC()
        val (highByte, lowByte) = pcRegisterPairValue.toBytes()
        Registers.specialPurposeRegisters.setSP(Registers.specialPurposeRegisters.getSP().minus(2).toShort())
        Memory.memorySet.setMemoryCells(
            Registers.specialPurposeRegisters.getSP().toUShort(),
            byteArrayOf(lowByte, highByte)
        )

        Registers.specialPurposeRegisters.setPC(restartAddress.toShort())
    }

    override fun toString(): String = "RST ${restartAddress.displayString()}"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11ttt111")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val restartAddress = bitPattern.getRSTOffset(word, 't')

            return RSTp(address, bytes, restartAddress)
        }
    }
}
