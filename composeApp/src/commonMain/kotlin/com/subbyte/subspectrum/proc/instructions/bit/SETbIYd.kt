package com.subbyte.subspectrum.proc.instructions.bit

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayStringDisplacement
import com.subbyte.subspectrum.units.setBit

data class SETbIYd(
    override val address: Address,
    override val bytes: DataByteArray,
    val bitPosition: Int,
    val displacement: Byte,
    val destinationRegister: RegisterCode? = null
) : Instruction {
    override fun getTStates(): Int = 23

    override fun execute() {
        val iyValue = Registers.specialPurposeRegisters.getIY()
        val targetAddress = iyValue.plus(displacement).toUShort()
        val memoryValue = Memory.memorySet.getMemoryCell(targetAddress)
        val newValue = memoryValue.setBit(bitPosition, true)
        Memory.memorySet.setMemoryCell(targetAddress, newValue)
        destinationRegister?.let { Registers.registerSet.setRegister(it, newValue) }
    }

    override fun toString(): String = "SET $bitPosition, (IY${displacement.displayStringDisplacement()})${destinationRegister?.let { ", $it" } ?: ""}"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11111101 11001011 dddddddd 11bbbrrr")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val bitPosition = bitPattern.getBitPosition(word, 'b')
            val displacement = bitPattern.getByte(word, 'd')
            val destinationRegister = bitPattern.getRegisterCodeOrNull(word, 'r')

            return SETbIYd(address, bytes, bitPosition, displacement, destinationRegister)
        }
    }
}
