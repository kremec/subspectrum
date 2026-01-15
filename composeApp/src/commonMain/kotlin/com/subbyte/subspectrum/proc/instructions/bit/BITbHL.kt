package com.subbyte.subspectrum.proc.instructions.bit

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.getBit

data class BITbHL(
    override val address: Address,
    override val bytes: ByteArray,
    val bit: Int
) : Instruction {
    override fun execute() {
        val hlValue = Registers.registerSet.getHL()
        val memoryValue = Memory.memorySet.getMemoryCell(hlValue.toUShort())
        val bitValue = memoryValue.getBit(bit)

        Registers.registerSet.setZFlag(!bitValue)
        Registers.registerSet.setHFlag(true)
        Registers.registerSet.setNFlag(false)
        // S, P/V unknown
    }

    override fun toString(): String = "BIT $bit, (HL)"

    companion object : InstructionDefinition {
        override val mCycles: Int = 3
        override val tStates: Int = 12

        override val bitPattern = BitPattern.of("11001011 01bbb110")
        override fun decode(word: Long, address: Address): Instruction {
            val b = bitPattern.get(word, 'b')

            val bit = b

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return BITbHL(address, bytes, bit)
        }
    }
}