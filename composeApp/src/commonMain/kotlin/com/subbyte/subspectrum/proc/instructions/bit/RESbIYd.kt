package com.subbyte.subspectrum.proc.instructions.bit

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.setBit

data class RESbIYd(
    override val address: Address,
    override val bytes: ByteArray,
    val bit: Int,
    val displacement: Byte
) : Instruction {
    override fun execute() {
        val iyValue = Registers.specialPurposeRegisters.getIY()
        val targetAddress = iyValue.plus(displacement).toUShort()
        val memoryValue = Memory.memorySet.getMemoryCell(targetAddress)
        val newValue = memoryValue.setBit(bit, false)
        Memory.memorySet.setMemoryCell(targetAddress, newValue)
        // No flags affected
    }

    override fun toString(): String = "RES $bit, (IY + $displacement)"

    companion object : InstructionDefinition {
        override val mCycles: Int = 4
        override val tStates: Int = 20

        override val bitPattern = BitPattern.of("11111101 11001011 dddddddd 10bbb110")
        override fun decode(word: Long, address: Address): Instruction {
            val b = bitPattern.get(word, 'b')
            val d = bitPattern.get(word, 'd')

            val bit = b
            val displacement = d.toByte()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return RESbIYd(address, bytes, bit, displacement)
        }
    }
}
