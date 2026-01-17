package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class CPn(
    override val address: Address,
    override val bytes: ByteArray,
    val sourceByte: Byte
) : Instruction {
    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        
        val a = aRegisterValue.toUByte().toInt()
        val source = sourceByte.toUByte().toInt()
        val diff = a - source
        val comparison = diff.toByte()

        val signFlag = comparison < 0
        val zeroFlag = comparison == 0.toByte()
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

    override fun toString(): String = "CP $sourceByte"

    companion object : InstructionDefinition {
        override val mCycles: Int = 2
        override val tStates: Int = 7

        override val bitPattern = BitPattern.of("11111110 nnnnnnnn")
        override fun decode(word: Long, address: Address): Instruction {
            val n = bitPattern.get(word, 'n')

            val sourceByte = n.toByte()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return CPn(address, bytes, sourceByte)
        }
    }
}
