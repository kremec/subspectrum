package com.subbyte.subspectrum.proc.instructions.shift

import com.subbyte.subspectrum.units.getBit
import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class RRD(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 18

    override fun execute() {
        val hlRegisterPairValue = Registers.registerSet.getHL()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(hlRegisterPairValue.toUShort())
        val aRegisterValue = Registers.registerSet.getA()

        val memoryLowNibble = sourceMemoryValue.toInt() and 0x0F
        val memoryHighNibble = (sourceMemoryValue.toInt() shr 4) and 0x0F
        val aRegisterLowNibble = aRegisterValue.toInt() and 0x0F
        val aRegisterHighNibble = (aRegisterValue.toInt() shr 4) and 0x0F

        val memoryResult = ((aRegisterLowNibble shl 4) or memoryHighNibble).toByte()
        val registerResult = ((aRegisterHighNibble shl 4) or memoryLowNibble).toByte()

        Memory.memorySet.setMemoryCell(hlRegisterPairValue.toUShort(), memoryResult)
        Registers.registerSet.setA(registerResult)

        Registers.registerSet.setSFlag(registerResult < 0)
        Registers.registerSet.setZFlag(registerResult == 0.toByte())
        Registers.registerSet.setYFFlag((registerResult).getBit(5))
        Registers.registerSet.setXFFlag((registerResult).getBit(3))
        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setPVFlag(registerResult.countOneBits() % 2 == 0)
        Registers.registerSet.setNFlag(false)
    }

    override fun toString(): String = "RRD"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 01100111")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return RRD(address, bytes)
        }
    }
}
