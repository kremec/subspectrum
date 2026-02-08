package com.subbyte.subspectrum.proc.instructions.load16

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterPairQQCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class POPqq(
    override val address: Address,
    override val bytes: DataByteArray,
    val destinationRegisterPairCode: RegisterPairQQCode
) : Instruction {
    override fun getTStates(): Int = 10

    override fun execute() {
        val sourceLowValue = Memory.memorySet.getMemoryCell(Registers.specialPurposeRegisters.getSP().toUShort())
        Registers.specialPurposeRegisters.setSP(Registers.specialPurposeRegisters.getSP().inc())
        val sourceHighValue = Memory.memorySet.getMemoryCell(Registers.specialPurposeRegisters.getSP().toUShort())
        Registers.specialPurposeRegisters.setSP(Registers.specialPurposeRegisters.getSP().inc())

        val sourceValue = ((sourceHighValue.toInt() shl 8) or (sourceLowValue.toInt() and 0xFF)).toShort()
        Registers.setRegisterPair(destinationRegisterPairCode, sourceValue)
    }

    override fun toString(): String = "PUSH $destinationRegisterPairCode"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11qq0001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val destinationRegisterPair = bitPattern.getRegisterPairQQCode(word, 'q')

            return POPqq(address, bytes, destinationRegisterPair)
        }
    }
}
