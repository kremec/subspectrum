package com.subbyte.subspectrum.proc.instructions.call

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.fromBytes

data class RET(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val spRegisterValue = Registers.specialPurposeRegisters.getSP()
        val bytes = Memory.memorySet.getMemoryCells(spRegisterValue.toUShort(), spRegisterValue.plus(1).toUShort())
        Registers.specialPurposeRegisters.setSP(spRegisterValue.plus(2).toShort())
        Registers.specialPurposeRegisters.setPC(Pair(bytes[1], bytes[0]).fromBytes())
    }

    override fun toString(): String = "RET"

    companion object : InstructionDefinition {
        override val mCycles: Int = 3
        override val tStates: Int = 10

        override val bitPattern = BitPattern.of("11001001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return RET(address, bytes)
        }
    }
}
