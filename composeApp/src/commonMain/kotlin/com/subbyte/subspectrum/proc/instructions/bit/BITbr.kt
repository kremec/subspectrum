package com.subbyte.subspectrum.proc.instructions.bit

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.getBit

data class BITbr(
    override val address: Address,
    override val bytes: ByteArray,
    val bit: Int,
    val sourceRegister: RegisterCode
) : Instruction {
    override fun execute() {
        val sourceValue = Registers.registerSet.getRegister(sourceRegister)
        val bitValue = sourceValue.getBit(bit)

        Registers.registerSet.setZFlag(!bitValue)
        Registers.registerSet.setHFlag(true)
        Registers.registerSet.setNFlag(false)
        // S, P/V unknown
    }

    override fun toString(): String = "BIT $bit, $sourceRegister"

    companion object : InstructionDefinition {
        override val mCycles: Int = 2
        override val tStates: Int = 8

        override val bitPattern = BitPattern.of("11001011 01bbbrrr")
        override fun decode(word: Long, address: Address): Instruction {
            val b = bitPattern.get(word, 'b')
            val r = bitPattern.get(word, 'r')

            val bit = b
            val sourceRegister = RegisterCode.entries.first { it.code == r }

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return BITbr(address, bytes, bit, sourceRegister)
        }
    }
}