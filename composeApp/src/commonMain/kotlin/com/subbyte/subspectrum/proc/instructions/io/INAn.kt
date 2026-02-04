package com.subbyte.subspectrum.proc.instructions.io

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class INAn(
    override val address: Address,
    override val bytes: ByteArray,
    val sourceByte: Byte
) : Instruction {
    override fun execute() {
        val sourceIOPortValue = IO.ioPortSet.getIOPort(sourceByte.toUByte())
        Registers.registerSet.setA(sourceIOPortValue)
    }

    override fun toString(): String = "IN A, (${sourceByte.toHexString(HexFormat.UpperCase)}h)"

    companion object : InstructionDefinition {
        override val mCycles: Int = 3
        override val tStates: Int = 11

        override val bitPattern = BitPattern.of("11011011 nnnnnnnn")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            val n = bitPattern.get(word, 'n')

            val sourceByte = n.toByte()

            return INAn(address, bytes, sourceByte)
        }
    }
}