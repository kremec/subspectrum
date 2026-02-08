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

data class CALLnn(
    override val address: Address,
    override val bytes: DataByteArray,
    val targetAddress: Address
) : Instruction {
    override fun getTStates(): Int = 17

    override fun execute() {
        val pcRegisterValue = Registers.specialPurposeRegisters.getPC()
        val (highByte, lowByte) = pcRegisterValue.toBytes()
        Registers.specialPurposeRegisters.setSP(Registers.specialPurposeRegisters.getSP().minus(2).toShort())
        Memory.memorySet.setMemoryCells(
            Registers.specialPurposeRegisters.getSP().toUShort(),
            byteArrayOf(lowByte, highByte)
        )

        Registers.specialPurposeRegisters.setPC(targetAddress.toShort())
    }

    override fun toString(): String = "CALL ${targetAddress.displayString()}"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11001101 llllllll hhhhhhhh")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val targetAddress = bitPattern.getUWord(word, 'l', 'h')

            return CALLnn(address, bytes, targetAddress)
        }
    }
}
