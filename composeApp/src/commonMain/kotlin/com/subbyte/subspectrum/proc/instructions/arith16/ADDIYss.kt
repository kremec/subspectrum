package com.subbyte.subspectrum.proc.instructions.arith16

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterPairCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class ADDIYss(
    override val address: Address,
    override val bytes: ByteArray,
    val sourceRegisterPairCode: RegisterPairCode
) : Instruction {
    override fun execute() {
        val iyRegisterPairValue = Registers.specialPurposeRegisters.getIY()
        val sourceValue = Registers.getRegisterPair(sourceRegisterPairCode)

        val iy = iyRegisterPairValue.toUShort().toInt()
        val source = sourceValue.toUShort().toInt()
        val sum = iy + source
        val result = sum.toShort()

        Registers.specialPurposeRegisters.setIY(result)

        val halfCarryFlag = ((iy and 0xFFF) + (source and 0xFFF)) > 0xFFF
        val carryFlag = sum > 0xFFFF
        Registers.registerSet.setHFlag(halfCarryFlag)
        Registers.registerSet.setNFlag(false)
        Registers.registerSet.setCFlag(carryFlag)
    }

    override fun toString(): String = "ADD IY, $sourceRegisterPairCode"

    companion object Companion : InstructionDefinition {
        override val mCycles: Int = 4
        override val tStates: Int = 15

        override val bitPattern = BitPattern.of("11111101 00ss1001")
        override fun decode(word: Long, address: Address): Instruction {
            val s = bitPattern.get(word, 's')

            val sourceRegisterPair = RegisterPairCode.entries.first { it.code == s }

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return ADDIYss(address, bytes, sourceRegisterPair)
        }
    }
}