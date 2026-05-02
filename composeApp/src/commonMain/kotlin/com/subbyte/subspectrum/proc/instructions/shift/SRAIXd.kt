package com.subbyte.subspectrum.proc.instructions.shift

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayStringDisplacement
import com.subbyte.subspectrum.units.getBit

data class SRAIXd(
    override val address: Address,
    override val bytes: DataByteArray,

    val displacement: Byte,
    val destinationRegister: RegisterCode? = null
) : Instruction {
    override fun getTStates(): Int = 23

    override fun execute() {
        val ixRegisterPairValue = Registers.specialPurposeRegisters.getIX()
        val sourceValue = Memory.memorySet.getMemoryCell(ixRegisterPairValue.plus(displacement).toUShort())
        val carryValue = sourceValue.getBit(0)
        val result = (sourceValue.toInt() shr 1).toByte()
        Memory.memorySet.setMemoryCell(ixRegisterPairValue.plus(displacement).toUShort(), result)
        destinationRegister?.let { Registers.registerSet.setRegister(it, result) }

        Registers.registerSet.setSFlag(result < 0)
        Registers.registerSet.setZFlag(result == 0.toByte())
        Registers.registerSet.setYFFlag((result).getBit(5))
        Registers.registerSet.setXFFlag((result).getBit(3))
        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setPVFlag(result.countOneBits() % 2 == 0)
        Registers.registerSet.setNFlag(false)
        Registers.registerSet.setCFlag(carryValue)
    }

    override fun toString(): String = "SRA (IX${displacement.displayStringDisplacement()})${destinationRegister?.let { ", $it" } ?: ""}"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11011101 11001011 dddddddd 00101rrr")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val displacement = bitPattern.getByte(word, 'd')
            val destinationRegister = bitPattern.getRegisterCodeOrNull(word, 'r')

            return SRAIXd(address, bytes, displacement, destinationRegister)
        }
    }
}
