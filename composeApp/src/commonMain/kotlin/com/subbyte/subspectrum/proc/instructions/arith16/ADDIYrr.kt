package com.subbyte.subspectrum.proc.instructions.arith16

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterPairRRCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray
data class ADDIYrr(
    override val address: Address,
    override val bytes: DataByteArray,
    val sourceRegisterPairCode: RegisterPairRRCode
) : Instruction {
    override fun getTStates(): Int = 15

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
        override val bitPattern = BitPattern.of("11111101 00ss1001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val sourceRegisterPair = bitPattern.getRegisterPairRRCode(word, 's')

            return ADDIYrr(address, bytes, sourceRegisterPair)
        }
    }
}
