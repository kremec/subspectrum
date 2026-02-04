package com.subbyte.subspectrum.proc.instructions.io

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class OTDR(
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

        Registers.registerSet.setZFlag(true)
        Registers.registerSet.setNFlag(true)

        if (newBValue != 0.toByte()) {
            Registers.specialPurposeRegisters.setPC(Registers.specialPurposeRegisters.getPC().minus(2).toShort())
        }

        // TODO: Interrupts are recognized and two refresh cycles execute after each data transfer
    }

    override fun toString(): String = "OTDR"

    companion object : InstructionDefinition {
        // TODO: Different timings based on B != 0 or B == 0
        override val mCycles: Int = 5
        override val tStates: Int = 21
        // override val mCycles: Int = 4
        // override val tStates: Int = 16

        override val bitPattern = BitPattern.of("11101101 10111011")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return OTDR(address, bytes)
        }
    }
}
