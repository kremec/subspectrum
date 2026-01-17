package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class DECIXd(
    override val address: Address,
    override val bytes: ByteArray,
    val displacement: Byte
) : Instruction {
    override fun execute() {
        val ixRegisterValue = Registers.specialPurposeRegisters.getIX()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(ixRegisterValue.plus(displacement).toUShort())
        
        val source = sourceMemoryValue.toUByte().toInt()
        val diff = source - 1
        val result = diff.toByte()
        
        Memory.memorySet.setMemoryCell(ixRegisterValue.plus(displacement).toUShort(), result)

        val signFlag = result < 0
        val zeroFlag = result == 0.toByte()
        val halfCarryFlag = (source and 0x0F) == 0x00
        val overflowFlag = source == 0x80
        Registers.registerSet.setSFlag(signFlag)
        Registers.registerSet.setZFlag(zeroFlag)
        Registers.registerSet.setHFlag(halfCarryFlag)
        Registers.registerSet.setPVFlag(overflowFlag)
        Registers.registerSet.setNFlag(true)
    }

    override fun toString(): String = "DEC (IX + $displacement)"

    companion object : InstructionDefinition {
        override val mCycles: Int = 6
        override val tStates: Int = 23

        override val bitPattern = BitPattern.of("11011101 00110101 dddddddd")
        override fun decode(word: Long, address: Address): Instruction {
            val d = bitPattern.get(word, 'd')

            val displacement = d.toByte()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return DECIXd(address, bytes, displacement)
        }
    }
}
