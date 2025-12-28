package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class LDADE(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val deRegisterValue = Registers.registerSet.getDE()
        val sourceValue = Memory.memorySet.getMemoryCell(deRegisterValue.toUShort())
        Registers.registerSet.setA(sourceValue)
    }

    override fun toString(): String = "LD A, (DE)"

    companion object : InstructionDefinition {
        override val mCycles: Int = 2
        override val tStates: Int = 7

        override val bitPattern = BitPattern.of("00001010")
        override fun decode(word: Long, address: Address): Instruction {

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return LDADE(address, bytes)
        }
    }
}
