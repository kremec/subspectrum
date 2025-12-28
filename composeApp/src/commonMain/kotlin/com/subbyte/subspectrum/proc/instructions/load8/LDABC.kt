package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class LDABC(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val bcRegisterValue = Registers.registerSet.getBC()
        val sourceValue = Memory.memorySet.getMemoryCell(bcRegisterValue.toUShort())
        Registers.registerSet.setA(sourceValue)
    }

    override fun toString(): String = "LD A, (BC)"

    companion object : InstructionDefinition {
        override val mCycles: Int = 2
        override val tStates: Int = 7

        override val bitPattern = BitPattern.of("00001010")
        override fun decode(word: Long, address: Address): Instruction {

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return LDABC(address, bytes)
        }
    }
}