package com.subbyte.subspectrum.proc.instructions.bit

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.setBit

data class RESbr(
    override val address: Address,
    override val bytes: ByteArray,
    val bit: Int,
    val sourceRegister: RegisterCode
) : Instruction {
    override fun execute() {
        val currentValue = Registers.registerSet.getRegister(sourceRegister)
        val newValue = currentValue.setBit(bit, false)
        Registers.registerSet.setRegister(sourceRegister, newValue)
    }

    override fun toString(): String = "RES $bit, $sourceRegister"

    companion object : InstructionDefinition {
        override val mCycles: Int = 2
        override val tStates: Int = 8

        override val bitPattern = BitPattern.of("11001011 10bbbrrr")
        override fun decode(word: Long, address: Address): Instruction {
            val b = bitPattern.get(word, 'b')
            val r = bitPattern.get(word, 'r')

            val bit = b
            val sourceRegister = RegisterCode.entries.first { it.code == r }

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return RESbr(address, bytes, bit, sourceRegister)
        }
    }
}
