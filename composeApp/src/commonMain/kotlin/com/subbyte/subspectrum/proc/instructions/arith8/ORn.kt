package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import kotlin.experimental.or

data class ORn(
    override val address: Address,
    override val bytes: ByteArray,
    val sourceByte: Byte
) : Instruction {
    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val result = aRegisterValue.or(sourceByte)
        Registers.registerSet.setA(result)

        val signFlag = result < 0
        val zeroFlag = result == 0.toByte()
        val parityFlag = result.countOneBits() % 2 == 0
        Registers.registerSet.setSFlag(signFlag)
        Registers.registerSet.setZFlag(zeroFlag)
        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setPVFlag(parityFlag)
        Registers.registerSet.setNFlag(false)
        Registers.registerSet.setCFlag(false)
    }

    override fun toString(): String = "OR $sourceByte"

    companion object : InstructionDefinition {
        override val mCycles: Int = 2
        override val tStates: Int = 7

        override val bitPattern = BitPattern.of("11110110 nnnnnnnn")
        override fun decode(word: Long, address: Address): Instruction {
            val n = bitPattern.get(word, 'n')

            val sourceByte = n.toByte()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return ORn(address, bytes, sourceByte)
        }
    }
}
