package com.subbyte.subspectrum.proc.instructions.load16

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.wordFromBytes

data class POPIY(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 14

    override fun execute() {
        val sourceLowValue = Memory.memorySet.getMemoryCell(Registers.specialPurposeRegisters.getSP().toUShort())
        Registers.specialPurposeRegisters.setSP(Registers.specialPurposeRegisters.getSP().inc())
        val sourceHighValue = Memory.memorySet.getMemoryCell(Registers.specialPurposeRegisters.getSP().toUShort())
        Registers.specialPurposeRegisters.setSP(Registers.specialPurposeRegisters.getSP().inc())

        val sourceValue = Pair(sourceHighValue, sourceLowValue).wordFromBytes()
        Registers.specialPurposeRegisters.setIY(sourceValue)
    }

    override fun toString(): String = "POP IY"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11111101 11100001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return POPIY(address, bytes)
        }
    }
}
