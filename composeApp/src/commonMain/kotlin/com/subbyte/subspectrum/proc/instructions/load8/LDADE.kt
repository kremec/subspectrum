package com.subbyte.subspectrum.proc.instructions.load8

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class LDADE(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 7

    override fun execute() {
        val deRegisterPairValue = Registers.registerSet.getDE()
        val sourceValue = Memory.memorySet.getMemoryCell(deRegisterPairValue.toUShort())
        Registers.registerSet.setA(sourceValue)
    }

    override fun toString(): String = "LD A, (DE)"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("00011010")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return LDADE(address, bytes)
        }
    }
}
