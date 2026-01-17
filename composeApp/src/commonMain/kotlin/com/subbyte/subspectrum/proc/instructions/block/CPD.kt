package com.subbyte.subspectrum.proc.instructions.block

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class CPD(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val hlRegisterPairValue = Registers.registerSet.getHL()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(hlRegisterPairValue.toUShort())
        val bcRegisterPairValue = Registers.registerSet.getBC()

        val comparison = aRegisterValue.minus(sourceMemoryValue).toByte()

        Registers.registerSet.setHL(hlRegisterPairValue.dec())
        Registers.registerSet.setBC(bcRegisterPairValue.dec())

        Registers.registerSet.setSFlag(comparison < 0.toByte())
        Registers.registerSet.setZFlag(comparison == 0.toByte())
        Registers.registerSet.setHFlag(((aRegisterValue.toUByte().toInt() and 0x0F) - (sourceMemoryValue.toUByte().toInt() and 0x0F)) < 0)
        Registers.registerSet.setPVFlag(bcRegisterPairValue.dec() != 0.toShort())
        Registers.registerSet.setNFlag(true)
    }

    override fun toString(): String = "CPD"

    companion object : InstructionDefinition {
        override val mCycles: Int = 4
        override val tStates: Int = 16

        override val bitPattern = BitPattern.of("11101101 10101001")
        override fun decode(word: Long, address: Address): Instruction {

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return CPD(address, bytes)
        }
    }
}