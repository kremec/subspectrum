package com.subbyte.subspectrum.proc.instructions.jump

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class JRd(
    override val address: Address,
    override val bytes: ByteArray,
    val displacement: Byte
) : Instruction {
    override fun execute() {
        val baseAddress = address.toShort()
        val newPC = baseAddress.plus(displacement).toShort()
        Registers.specialPurposeRegisters.setPC(newPC)
    }

    override fun toString(): String {
        val disp = displacement.toInt()
        val sign = if (disp >= 0) "+" else ""
        return "JR $sign$disp"
    }

    companion object Companion : InstructionDefinition {
        override val mCycles: Int = 3
        override val tStates: Int = 12

        override val bitPattern = BitPattern.of("00011000 dddddddd")
        override fun decode(word: Long, address: Address): Instruction {
            val d = bitPattern.get(word, 'd')

            val displacement = d.toByte()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return JRd(address, bytes, displacement)
        }
    }
}