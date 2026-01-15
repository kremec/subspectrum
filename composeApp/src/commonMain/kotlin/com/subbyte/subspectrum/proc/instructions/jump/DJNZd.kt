package com.subbyte.subspectrum.proc.instructions.jump

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class DJNZd(
    override val address: Address,
    override val bytes: ByteArray,
    val displacement: Byte
) : Instruction {
    override fun execute() {
        val bRegisterValue = Registers.registerSet.getB()
        val result = bRegisterValue.minus(1).toByte()
        Registers.registerSet.setB(result)

        if (result != 0.toByte()) {
            val baseAddress = address.toShort()
            val newPC = baseAddress + displacement
            Registers.specialPurposeRegisters.setPC(newPC.toShort())
        }
    }

    override fun toString(): String {
        val disp = displacement.toInt()
        val sign = if (disp >= 0) "+" else ""
        return "DJNZ $sign$disp"
    }

    companion object Companion : InstructionDefinition {
        // TODO: Different timings based on whether jump occurs
        override val mCycles: Int = 3  // When jump occurs
        override val tStates: Int = 13 // When jump occurs
        // override val mCycles: Int = 2  // When no jump
        // override val tStates: Int = 8  // When no jump

        override val bitPattern = BitPattern.of("00010000 dddddddd")
        override fun decode(word: Long, address: Address): Instruction {
            val d = bitPattern.get(word, 'd')

            val displacement = d.toByte()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return DJNZd(address, bytes, displacement)
        }
    }
}