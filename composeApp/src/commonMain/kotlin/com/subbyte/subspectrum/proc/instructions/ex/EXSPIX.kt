package com.subbyte.subspectrum.proc.instructions.ex

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.toBytes
import com.subbyte.subspectrum.units.wordFromBytes

data class EXSPIX(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 23

    override fun execute() {
        val ixRegisterPairValue = Registers.specialPurposeRegisters.getIX()
        val (sourceRegisterHighValue, sourceRegisterLowValue) = ixRegisterPairValue.toBytes()
        val spRegisterValue = Registers.specialPurposeRegisters.getSP()
        val sourceMemoryLowValue = Memory.memorySet.getMemoryCell(spRegisterValue.toUShort())
        val sourceMemoryHighValue = Memory.memorySet.getMemoryCell(spRegisterValue.inc().toUShort())

        Registers.specialPurposeRegisters.setIX(Pair(sourceMemoryHighValue, sourceMemoryLowValue).wordFromBytes())
        Memory.memorySet.setMemoryCells(spRegisterValue.toUShort(), byteArrayOf(sourceRegisterLowValue, sourceRegisterHighValue))
    }

    override fun toString(): String = "EX (SP), IX"


    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11011101 11100011")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return EXSPIX(address, bytes)
        }
    }
}
