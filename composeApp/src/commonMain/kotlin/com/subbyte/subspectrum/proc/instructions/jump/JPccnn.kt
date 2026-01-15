package com.subbyte.subspectrum.proc.instructions.jump

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.ConditionCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.fromBytes

data class JPccnn(
    override val address: Address,
    override val bytes: ByteArray,
    val condition: ConditionCode,
    val targetAddress: Address
) : Instruction {
    override fun execute() {
        if (Registers.registerSet.checkCondition(condition)) {
            Registers.specialPurposeRegisters.setPC(targetAddress.toShort())
        }
    }

    override fun toString(): String = "JP $condition, ${targetAddress.toString(16).uppercase().padStart(4, '0')}h"

    companion object : InstructionDefinition {
        override val mCycles: Int = 3
        override val tStates: Int = 10

        override val bitPattern = BitPattern.of("11ccc010 llllllll hhhhhhhh")
        override fun decode(word: Long, address: Address): Instruction {
            val c = bitPattern.get(word, 'c')
            val l = bitPattern.get(word, 'l').toByte()
            val h = bitPattern.get(word, 'h').toByte()

            val condition = ConditionCode.entries.first { it.code == c }
            val targetAddress = Pair(h, l).fromBytes().toUShort()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return JPccnn(address, bytes, condition, targetAddress)
        }
    }
}