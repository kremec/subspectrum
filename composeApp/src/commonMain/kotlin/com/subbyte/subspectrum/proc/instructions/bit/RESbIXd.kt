package com.subbyte.subspectrum.proc.instructions.bit

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.setBit

data class RESbIXd(
    override val address: Address,
    override val bytes: ByteArray,
    val bit: Int,
    val displacement: Byte
) : Instruction {
    override fun execute() {
        val ixValue = Registers.specialPurposeRegisters.getIX()
        val targetAddress = ixValue.plus(displacement).toUShort()
        val memoryValue = Memory.memorySet.getMemoryCell(targetAddress)
        val newValue = memoryValue.setBit(bit, false)
        Memory.memorySet.setMemoryCell(targetAddress, newValue)
    }

    override fun toString(): String = "RES $bit, (IX + $displacement)"

    companion object : InstructionDefinition {
        override val mCycles: Int = 4
        override val tStates: Int = 20

        override val bitPattern = BitPattern.of("11011101 11001011 dddddddd 10bbb110")
        override fun decode(word: Long, address: Address): Instruction {
            val b = bitPattern.get(word, 'b')
            val d = bitPattern.get(word, 'd')

            val bit = b
            val displacement = d.toByte()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return RESbIXd(address, bytes, bit, displacement)
        }
    }
}
