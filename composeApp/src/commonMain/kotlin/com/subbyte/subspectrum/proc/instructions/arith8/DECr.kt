package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class DECr(
    override val address: Address,
    override val bytes: ByteArray,
    val sourceRegister: RegisterCode
) : Instruction {
    override fun execute() {
        val sourceRegisterValue = Registers.registerSet.getRegister(sourceRegister)
        
        val source = sourceRegisterValue.toUByte().toInt()
        val diff = source - 1
        val result = diff.toByte()
        
        Registers.registerSet.setRegister(sourceRegister, result)

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

    override fun toString(): String = "DEC $sourceRegister"

    companion object : InstructionDefinition {
        override val mCycles: Int = 1
        override val tStates: Int = 4

        override val bitPattern = BitPattern.of("00rrr101")
        override fun decode(word: Long, address: Address): Instruction {
            val r = bitPattern.get(word, 'r')

            val sourceRegister = RegisterCode.entries.first { it.code == r }

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return DECr(address, bytes, sourceRegister)
        }
    }
}
