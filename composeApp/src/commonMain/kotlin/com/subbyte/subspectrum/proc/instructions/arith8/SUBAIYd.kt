package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class SUBAIYd(
    override val address: Address,
    override val bytes: ByteArray,
    val displacement: Byte
) : Instruction {
    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val iyRegisterPairValue = Registers.specialPurposeRegisters.getIY()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(iyRegisterPairValue.plus(displacement).toUShort())
        
        val a = aRegisterValue.toUByte().toInt()
        val source = sourceMemoryValue.toUByte().toInt()
        val diff = a - source
        val result = diff.toByte()
        
        Registers.registerSet.setA(result)

        val signFlag = result < 0
        val zeroFlag = result == 0.toByte()
        val halfCarryFlag = ((a and 0x0F) - (source and 0x0F)) < 0
        val overflowFlag = ((a xor source) and (a xor diff) and 0x80) != 0
        val carryFlag = diff < 0
        Registers.registerSet.setSFlag(signFlag)
        Registers.registerSet.setZFlag(zeroFlag)
        Registers.registerSet.setHFlag(halfCarryFlag)
        Registers.registerSet.setPVFlag(overflowFlag)
        Registers.registerSet.setNFlag(true)
        Registers.registerSet.setCFlag(carryFlag)
    }

    override fun toString(): String = "SUB A, (IY + ${displacement})"

    companion object : InstructionDefinition {
        override val mCycles: Int = 5
        override val tStates: Int = 19

        override val bitPattern = BitPattern.of("11111101 10010110 dddddddd")
        override fun decode(word: Long, address: Address): Instruction {
            val d = bitPattern.get(word, 'd')

            val displacement = d.toByte()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return SUBAIYd(address, bytes, displacement)
        }
    }
}
