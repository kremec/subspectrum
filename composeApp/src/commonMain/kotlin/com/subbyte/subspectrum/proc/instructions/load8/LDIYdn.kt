package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class LDIYdn(
    override val address: Address,
    override val bytes: ByteArray,
    val displacement: Byte,
    val sourceByte: Byte
) : Instruction {
    override fun execute() {
        val iyRegisterValue = Registers.specialPurposeRegisters.getIY()
        Memory.memorySet.setMemoryCell(iyRegisterValue.plus(displacement).toUShort(), sourceByte)
    }

    override fun toString(): String = "LD (IX+${displacement.toHexString(HexFormat.UpperCase)}h), ${sourceByte.toHexString(HexFormat.UpperCase)}h"

    companion object : InstructionDefinition {
        override val mCycles: Int = 5
        override val tStates: Int = 19

        override val bitPattern = BitPattern.of("11111101 00110110 dddddddd nnnnnnnn")
        override fun decode(word: Long, address: Address): Instruction {
            val d = bitPattern.get(word, 'd')
            val n = bitPattern.get(word, 'n')

            val displacement = d.toByte()
            val sourceByte = n.toByte()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return LDIYdn(address, bytes, displacement, sourceByte)
        }
    }
}