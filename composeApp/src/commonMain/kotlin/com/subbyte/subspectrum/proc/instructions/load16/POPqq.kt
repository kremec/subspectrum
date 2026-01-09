package com.subbyte.subspectrum.proc.instructions.load16

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterPairStackCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class POPqq(
    override val address: Address,
    override val bytes: ByteArray,
    val destinationRegisterPairCode: RegisterPairStackCode
) : Instruction {
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
        override val mCycles: Int = 3
        override val tStates: Int = 10

        override val bitPattern = BitPattern.of("11qq0001")
        override fun decode(word: Long, address: Address): Instruction {
            val q = bitPattern.get(word, 'q')

            val destinationRegisterPair = RegisterPairStackCode.entries.first { it.code == q }

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return POPqq(address, bytes, destinationRegisterPair)
        }
    }
}