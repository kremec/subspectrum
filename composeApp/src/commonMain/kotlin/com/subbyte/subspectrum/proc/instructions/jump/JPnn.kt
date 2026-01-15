package com.subbyte.subspectrum.proc.instructions.jump

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.fromBytes

data class JPnn(
    override val address: Address,
    override val bytes: ByteArray,
    val targetAddress: Address
) : Instruction {
    override fun execute() {
        Registers.specialPurposeRegisters.setPC(targetAddress.toShort())
    }

    override fun toString(): String = "JP ${targetAddress.toString(16).uppercase().padStart(4, '0')}h"

    companion object : InstructionDefinition {
        override val mCycles: Int = 3
        override val tStates: Int = 10

        override val bitPattern = BitPattern.of("11000011 llllllll hhhhhhhh")
        override fun decode(word: Long, address: Address): Instruction {
            val l = bitPattern.get(word, 'l').toByte()
            val h = bitPattern.get(word, 'h').toByte()

            val targetAddress = Pair(h, l).fromBytes().toUShort()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return JPnn(address, bytes, targetAddress)
        }
    }
}