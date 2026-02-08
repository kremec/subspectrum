package com.subbyte.subspectrum.proc.instructions.shift

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.getBit

data class RLCr(
    override val address: Address,
    override val bytes: DataByteArray,
    val sourceRegister: RegisterCode
) : Instruction {
    override fun getTStates(): Int = 8

    override fun execute() {
        val sourceRegisterValue = Registers.registerSet.getRegister(sourceRegister)
        val carryValue = sourceRegisterValue.getBit(7)
        val result = sourceRegisterValue.rotateLeft(1)
        Registers.registerSet.setRegister(sourceRegister, result)

        Registers.registerSet.setSFlag(result < 0)
        Registers.registerSet.setZFlag(result == 0.toByte())
        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setPVFlag(result.countOneBits() % 2 == 0)
        Registers.registerSet.setNFlag(false)
        Registers.registerSet.setCFlag(carryValue)
    }

    override fun toString(): String = "RLC $sourceRegister"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11001011 00000rrr")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val sourceRegister = bitPattern.getRegisterCode(word, 'r')

            return RLCr(address, bytes, sourceRegister)
        }
    }
}
