package com.subbyte.subspectrum.proc.instructions.bit

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.getBit

data class BITbIXd(
    override val address: Address,
    override val bytes: ByteArray,
    val bit: Int,
    val displacement: Byte
) : Instruction {
    override fun execute() {
        val ixValue = Registers.specialPurposeRegisters.getIX()
        val sourceValue = Memory.memorySet.getMemoryCell(ixValue.plus(displacement).toUShort())
        val bitValue = sourceValue.getBit(bit)

        Registers.registerSet.setZFlag(!bitValue)
        Registers.registerSet.setHFlag(true)
        Registers.registerSet.setNFlag(false)
        // S, P/V unknown
    }

    override fun toString(): String = "BIT $bit, (IX + $displacement)"

    companion object : InstructionDefinition {
        override val mCycles: Int = 5
        override val tStates: Int = 20

        override val bitPattern = BitPattern.of("11011101 11001011 dddddddd 01bbb110")
        override fun decode(word: Long, address: Address): Instruction {
            val b = bitPattern.get(word, 'b')
            val d = bitPattern.get(word, 'd')

            val bit = b
            val displacement = d.toByte()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return BITbIXd(address, bytes, bit, displacement)
        }
    }
}