package com.subbyte.subspectrum.proc.instructions.io

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class OUTCr(
    override val address: Address,
    override val bytes: ByteArray,
    val sourceRegister: RegisterCode
) : Instruction {
    override fun execute() {
        val sourceRegisterValue = Registers.registerSet.getRegister(sourceRegister)
        val cRegisterValue = Registers.registerSet.getC()
        IO.ioPortSet.setIOPort(cRegisterValue.toUByte(), sourceRegisterValue)
    }

    override fun toString(): String = "OUT (C), $sourceRegister"

    companion object : InstructionDefinition {
        override val mCycles: Int = 3
        override val tStates: Int = 12

        override val bitPattern = BitPattern.of("11101101 01rrr001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            val r = bitPattern.get(word, 'r')

            val sourceRegister = RegisterCode.entries.first { it.code == r }

            return OUTCr(address, bytes, sourceRegister)
        }
    }
}
