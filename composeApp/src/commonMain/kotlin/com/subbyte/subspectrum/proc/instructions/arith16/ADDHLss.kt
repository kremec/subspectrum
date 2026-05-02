package com.subbyte.subspectrum.proc.instructions.arith16

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterPairSSCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.getBit
import com.subbyte.subspectrum.units.toBytes

data class ADDHLss(
    override val address: Address,
    override val bytes: DataByteArray,
    val sourceRegisterPairCode: RegisterPairSSCode
) : Instruction {
    override fun getTStates(): Int = 11

    override fun execute() {
        val hlRegisterPairValue = Registers.registerSet.getHL()
        val sourceValue = Registers.getRegisterPair(sourceRegisterPairCode)

        val hl = hlRegisterPairValue.toUShort().toInt()
        val source = sourceValue.toUShort().toInt()
        val sum = hl + source
        val result = sum.toShort()

        Registers.specialPurposeRegisters.setMEMPTR(hlRegisterPairValue)
        Registers.registerSet.setHL(result)

        val halfCarryFlag = ((hl and 0xFFF) + (source and 0xFFF)) > 0xFFF
        val carryFlag = sum > 0xFFFF
        val resultHighByte = result.toBytes().first
        Registers.registerSet.setYFFlag(resultHighByte.getBit(5))
        Registers.registerSet.setXFFlag(resultHighByte.getBit(3))
        Registers.registerSet.setHFlag(halfCarryFlag)
        Registers.registerSet.setNFlag(false)
        Registers.registerSet.setCFlag(carryFlag)
    }

    override fun toString(): String = "ADD HL, $sourceRegisterPairCode"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("00ss1001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val sourceRegisterPair = bitPattern.getRegisterPairSSCode(word, 's')

            return ADDHLss(address, bytes, sourceRegisterPair)
        }
    }
}
