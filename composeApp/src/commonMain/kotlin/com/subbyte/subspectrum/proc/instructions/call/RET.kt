package com.subbyte.subspectrum.proc.instructions.call

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.wordFromBytes

data class RET(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 10

    override fun execute() {
        val spRegisterValue = Registers.specialPurposeRegisters.getSP()
        val bytes = Memory.memorySet.getMemoryCells(spRegisterValue.toUShort(), spRegisterValue.plus(1).toUShort())
        Registers.specialPurposeRegisters.setSP(spRegisterValue.plus(2).toShort())
        Registers.specialPurposeRegisters.setPC(Pair(bytes[1], bytes[0]).wordFromBytes())
    }

    override fun toString(): String = "RET"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11001001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return RET(address, bytes)
        }
    }
}
