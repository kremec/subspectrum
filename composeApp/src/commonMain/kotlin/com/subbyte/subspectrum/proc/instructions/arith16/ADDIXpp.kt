package com.subbyte.subspectrum.proc.instructions.arith16

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterPairPPCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.getBit
import com.subbyte.subspectrum.units.toBytes

data class ADDIXpp(
    override val address: Address,
    override val bytes: DataByteArray,
    val sourceRegisterPairCode: RegisterPairPPCode
) : Instruction {
    override fun getTStates(): Int = 15

    override fun execute() {
        val ixRegisterPairValue = Registers.specialPurposeRegisters.getIX()
        val sourceValue = Registers.getRegisterPair(sourceRegisterPairCode)

        val ix = ixRegisterPairValue.toUShort().toInt()
        val source = sourceValue.toUShort().toInt()
        val sum = ix + source
        val result = sum.toShort()

        Registers.specialPurposeRegisters.setIX(result)

        val halfCarryFlag = ((ix and 0xFFF) + (source and 0xFFF)) > 0xFFF
        val carryFlag = sum > 0xFFFF
        val resultHighByte = result.toBytes().first
        Registers.registerSet.setYFFlag(resultHighByte.getBit(5))
        Registers.registerSet.setXFFlag(resultHighByte.getBit(3))
        Registers.registerSet.setHFlag(halfCarryFlag)
        Registers.registerSet.setNFlag(false)
        Registers.registerSet.setCFlag(carryFlag)
    }

    override fun toString(): String = "ADD IX, $sourceRegisterPairCode"

    companion object Companion : InstructionDefinition {
        override val bitPattern = BitPattern.of("11011101 00ss1001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val sourceRegisterPair = bitPattern.getRegisterPairPPCode(word, 's')

            return ADDIXpp(address, bytes, sourceRegisterPair)
        }
    }
}
