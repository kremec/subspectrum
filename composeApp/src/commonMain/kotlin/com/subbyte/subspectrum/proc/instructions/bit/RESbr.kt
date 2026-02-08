package com.subbyte.subspectrum.proc.instructions.bit

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.setBit

data class RESbr(
    override val address: Address,
    override val bytes: DataByteArray,
    val bitPosition: Int,
    val sourceRegister: RegisterCode
) : Instruction {
    override fun getTStates(): Int = 8

    override fun execute() {
        val currentValue = Registers.registerSet.getRegister(sourceRegister)
        val newValue = currentValue.setBit(bitPosition, false)
        Registers.registerSet.setRegister(sourceRegister, newValue)
    }

    override fun toString(): String = "RES $bitPosition, $sourceRegister"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11001011 10bbbrrr")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val bitPosition = bitPattern.getBitPosition(word, 'b')
            val sourceRegister = bitPattern.getRegisterCode(word, 'r')

            return RESbr(address, bytes, bitPosition, sourceRegister)
        }
    }
}
