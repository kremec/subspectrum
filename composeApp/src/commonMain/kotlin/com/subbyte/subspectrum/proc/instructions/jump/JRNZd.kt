package com.subbyte.subspectrum.proc.instructions.jump

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class JRNZd(
    override val address: Address,
    override val bytes: ByteArray,
    val displacement: Byte
) : Instruction {
    override fun execute() {
        if (!Registers.registerSet.getZFlag()) {
            val baseAddress = address.toShort()
            val newPC = baseAddress + displacement
            Registers.specialPurposeRegisters.setPC(newPC.toShort())
        }
    }

    override fun toString(): String {
        val disp = displacement.toInt()
        val sign = if (disp >= 0) "+" else ""
        return "JR NZ, $sign$disp"
    }

    companion object Companion : InstructionDefinition {
        // TODO: Different timings based on Z flag clear or set
        override val mCycles: Int = 3  // When condition met
        override val tStates: Int = 12 // When condition met
        // override val mCycles: Int = 2  // When condition not met
        // override val tStates: Int = 7  // When condition not met

        override val bitPattern = BitPattern.of("00100000 dddddddd")
        override fun decode(word: Long, address: Address): Instruction {
            val d = bitPattern.get(word, 'd')

            val displacement = d.toByte()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return JRNZd(address, bytes, displacement)
        }
    }
}