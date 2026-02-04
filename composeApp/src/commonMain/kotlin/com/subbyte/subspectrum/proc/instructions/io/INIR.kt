package com.subbyte.subspectrum.proc.instructions.io

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class INIR(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val bRegisterValue = Registers.registerSet.getB()
        val cRegisterValue = Registers.registerSet.getC()
        val inputData = IO.ioPortSet.getIOPort(cRegisterValue.toUByte())

        val hlRegisterPairValue = Registers.registerSet.getHL()
        Memory.memorySet.setMemoryCell(hlRegisterPairValue.toUShort(), inputData)

        val newBValue = bRegisterValue.dec()
        Registers.registerSet.setB(newBValue)
        Registers.registerSet.setHL(hlRegisterPairValue.inc())

        Registers.registerSet.setZFlag(true)
        Registers.registerSet.setNFlag(true)

        if (newBValue != 0.toByte()) {
            Registers.specialPurposeRegisters.setPC(Registers.specialPurposeRegisters.getPC().minus(2).toShort())
        }

        // TODO: Interrupts are recognized and two refresh cycles execute after each data transfer
    }

    override fun toString(): String = "INIR"

    companion object : InstructionDefinition {
        // TODO: Different timings based on B != 0 or B == 0
        override val mCycles: Int = 5
        override val tStates: Int = 21
        // override val mCycles: Int = 4
        // override val tStates: Int = 16

        override val bitPattern = BitPattern.of("11101101 10110010")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return INIR(address, bytes)
        }
    }
}
