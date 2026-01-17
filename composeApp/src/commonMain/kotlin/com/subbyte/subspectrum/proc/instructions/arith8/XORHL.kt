package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import kotlin.experimental.xor

data class XORHL(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val hlRegisterPairValue = Registers.registerSet.getHL()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(hlRegisterPairValue.toUShort())
        val result = aRegisterValue.xor(sourceMemoryValue)
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

    override fun toString(): String = "XOR (HL)"

    companion object : InstructionDefinition {
        override val mCycles: Int = 2
        override val tStates: Int = 7

        override val bitPattern = BitPattern.of("10101110")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return XORHL(address, bytes)
        }
    }
}
