package com.subbyte.subspectrum.proc.instructions.arith16

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterPairCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class SBCHLss(
    override val address: Address,
    override val bytes: ByteArray,
    val sourceRegisterPairCode: RegisterPairCode
) : Instruction {
    override fun execute() {
        val hlRegisterPairValue = Registers.registerSet.getHL()
        val sourceValue = Registers.getRegisterPair(sourceRegisterPairCode)
        val carryValue = if (Registers.registerSet.getCFlag()) 1 else 0

        val hl = hlRegisterPairValue.toUShort().toInt()
        val source = sourceValue.toUShort().toInt()
        val diff = hl - source - carryValue
        val result = diff.toShort()

        Registers.registerSet.setHL(result)

        val signFlag = result < 0
        val zeroFlag = result == 0.toShort()
        val halfCarryFlag = ((hl and 0xFFF) - (source and 0xFFF) - carryValue) < 0
        val overflowFlag = ((hl xor source) and (hl xor diff) and 0x8000) != 0
        val carryFlag = diff < 0
        Registers.registerSet.setSFlag(signFlag)
        Registers.registerSet.setZFlag(zeroFlag)
        Registers.registerSet.setHFlag(halfCarryFlag)
        Registers.registerSet.setPVFlag(overflowFlag)
        Registers.registerSet.setNFlag(true)
        Registers.registerSet.setCFlag(carryFlag)
    }

    override fun toString(): String = "SBC HL, $sourceRegisterPairCode"

    companion object : InstructionDefinition {
        override val mCycles: Int = 4
        override val tStates: Int = 15

        override val bitPattern = BitPattern.of("11101101 01ss0010")
        override fun decode(word: Long, address: Address): Instruction {
            val s = bitPattern.get(word, 's')

            val sourceRegisterPair = RegisterPairCode.entries.first { it.code == s }

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return SBCHLss(address, bytes, sourceRegisterPair)
        }
    }
}