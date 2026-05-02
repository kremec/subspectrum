package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.units.getBit
import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.IndexedByteRemappable
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class INCr(
    override val address: Address,
    override val bytes: DataByteArray,
    val sourceRegister: RegisterCode
) : Instruction, IndexedByteRemappable {
    override fun getTStates(): Int = 4

    override fun execute() {
        val sourceRegisterValue = Registers.registerSet.getRegister(sourceRegister)

        val source = sourceRegisterValue.toUByte().toInt()
        val sum = source + 1
        val result = sum.toByte()

        Registers.registerSet.setRegister(sourceRegister, result)

        val signFlag = result < 0
        val zeroFlag = result == 0.toByte()
        val halfCarryFlag = (source and 0x0F) == 0x0F
        val overflowFlag = source == 0x7F
        Registers.registerSet.setSFlag(signFlag)
        Registers.registerSet.setZFlag(zeroFlag)
        Registers.registerSet.setYFFlag((result).getBit(5))
        Registers.registerSet.setXFFlag((result).getBit(3))
        Registers.registerSet.setHFlag(halfCarryFlag)
        Registers.registerSet.setPVFlag(overflowFlag)
        Registers.registerSet.setNFlag(false)
    }

    override fun toString(): String = "INC $sourceRegister"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("00rrr100")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val sourceRegister = bitPattern.getRegisterCode(word, 'r')

            return INCr(address, bytes, sourceRegister)
        }
    }
}
