package com.subbyte.subspectrum.proc.instructions.ex

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.fromBytes
import com.subbyte.subspectrum.units.toBytes

data class EXSPIX(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val ixRegisterPairValue = Registers.specialPurposeRegisters.getIX()
        val (sourceRegisterHighValue, sourceRegisterLowValue) = ixRegisterPairValue.toBytes()
        val spRegisterValue = Registers.specialPurposeRegisters.getSP()
        val sourceMemoryLowValue = Memory.memorySet.getMemoryCell(spRegisterValue.toUShort())
        val sourceMemoryHighValue = Memory.memorySet.getMemoryCell(spRegisterValue.inc().toUShort())

        Registers.specialPurposeRegisters.setIX(Pair(sourceMemoryHighValue, sourceMemoryLowValue).fromBytes())
        Memory.memorySet.setMemoryCells(spRegisterValue.toUShort(), byteArrayOf(sourceRegisterLowValue, sourceRegisterHighValue))
    }

    override fun toString(): String = "EX (SP), IX"

    companion object : InstructionDefinition {
        override val mCycles: Int = 6
        override val tStates: Int = 23

        override val bitPattern = BitPattern.of("11011101 11100011")
        override fun decode(word: Long, address: Address): Instruction {

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return EXSPIX(address, bytes)
        }
    }
}