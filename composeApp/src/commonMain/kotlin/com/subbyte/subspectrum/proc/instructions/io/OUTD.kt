package com.subbyte.subspectrum.proc.instructions.io

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class OUTD(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val hlRegisterPairValue = Registers.registerSet.getHL()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(hlRegisterPairValue.toUShort())

        val bRegisterValue = Registers.registerSet.getB()
        val cRegisterValue = Registers.registerSet.getC()
        IO.ioPortSet.setIOPort(cRegisterValue.toUByte(), sourceMemoryValue)

        val newBValue = bRegisterValue.dec()
        Registers.registerSet.setB(newBValue)
        Registers.registerSet.setHL(hlRegisterPairValue.dec())

        Registers.registerSet.setZFlag(newBValue == 0.toByte())
        Registers.registerSet.setNFlag(true)
    }

    override fun toString(): String = "OUTD"

    companion object : InstructionDefinition {
        override val mCycles: Int = 4
        override val tStates: Int = 16

        override val bitPattern = BitPattern.of("11101101 10101011")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return OUTD(address, bytes)
        }
    }
}
