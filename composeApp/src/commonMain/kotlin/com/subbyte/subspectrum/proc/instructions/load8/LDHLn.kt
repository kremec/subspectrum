package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class LDHLn(
    override val address: Address,
    override val bytes: ByteArray,
    val sourceByte: Byte
) : Instruction {
    override fun execute() {
        val hlRegisterValue = Registers.registerSet.getHL()
        Memory.memorySet.setMemoryCell(hlRegisterValue.toUShort(), sourceByte)
    }

    override fun toString(): String = "LD (HL), ${sourceByte.toHexString(HexFormat.UpperCase)}h"

    companion object : InstructionDefinition {
        override val mCycles: Int = 3
        override val tStates: Int = 10

        override val bitPattern = BitPattern.of("00110110 nnnnnnnn")
        override fun decode(word: Long, address: Address): Instruction {
            val n = bitPattern.get(word, 'n')

            val sourceByte = n.toByte()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return LDHLn(address, bytes, sourceByte)
        }
    }
}
