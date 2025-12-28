package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class LDrn(
    override val address: Address,
    override val bytes: ByteArray,
    val destinationRegister: RegisterCode,
    val sourceByte: Byte
) : Instruction {
    override fun execute() {
        Registers.registerSet.setRegister(destinationRegister, sourceByte)
    }

    override fun toString(): String = "LD $destinationRegister, ${sourceByte.toHexString(HexFormat.UpperCase)}h"

    companion object : InstructionDefinition {
        override val mCycles: Int = 2
        override val tStates: Int = 7

        override val bitPattern = BitPattern.of("00rrr110 nnnnnnnn")
        override fun decode(word: Long, address: Address): Instruction {
            val r = bitPattern.get(word, 'r')
            val n = bitPattern.get(word, 'n')

            val destinationRegister = RegisterCode.entries.first { it.code == r }
            val sourceByte = n.toByte()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return LDrn(address, bytes, destinationRegister, sourceByte)
        }
    }
}