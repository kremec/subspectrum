package com.subbyte.subspectrum.proc.instructions.arith16

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterPairSSCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class SBCHLss(
    override val address: Address,
    override val bytes: DataByteArray,
    val sourceRegisterPairCode: RegisterPairSSCode
) : Instruction {
    override fun getTStates(): Int = 15

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
        override val bitPattern = BitPattern.of("11101101 01ss0010")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val sourceRegisterPair = bitPattern.getRegisterPairSSCode(word, 's')

            return SBCHLss(address, bytes, sourceRegisterPair)
        }
    }
}
