package com.subbyte.subspectrum.proc.instructions.arith16

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterPairCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class ADCHLss(
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
        val sum = hl + source + carryValue
        val result = sum.toShort()

        Registers.registerSet.setHL(result)

        val signFlag = result < 0
        val zeroFlag = result == 0.toShort()
        val halfCarryFlag = ((hl and 0xFFF) + (source and 0xFFF) + carryValue) > 0xFFF
        val overflowFlag = ((hl xor sum) and (source xor sum) and 0x8000) != 0
        val carryFlag = sum > 0xFFFF
        Registers.registerSet.setSFlag(signFlag)
        Registers.registerSet.setZFlag(zeroFlag)
        Registers.registerSet.setHFlag(halfCarryFlag)
        Registers.registerSet.setPVFlag(overflowFlag)
        Registers.registerSet.setNFlag(false)
        Registers.registerSet.setCFlag(carryFlag)
    }

    override fun toString(): String = "ADC HL, $sourceRegisterPairCode"

    companion object : InstructionDefinition {
        override val mCycles: Int = 4
        override val tStates: Int = 15

        override val bitPattern = BitPattern.of("11101101 01ss1010")
        override fun decode(word: Long, address: Address): Instruction {
            val s = bitPattern.get(word, 's')

            val sourceRegisterPair = RegisterPairCode.entries.first { it.code == s }

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return ADCHLss(address, bytes, sourceRegisterPair)
        }
    }
}