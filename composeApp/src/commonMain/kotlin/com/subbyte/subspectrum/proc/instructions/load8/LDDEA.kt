package com.subbyte.subspectrum.proc.instructions.load8

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class LDDEA(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 7

    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val deRegisterPairValue = Registers.registerSet.getDE()
        Memory.memorySet.setMemoryCell(deRegisterPairValue.toUShort(), aRegisterValue)
    }

    override fun toString(): String = "LD (DE), A"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("00010010")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return LDDEA(address, bytes)
        }
    }
}
