package com.subbyte.subspectrum.proc.instructions.jump

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.ConditionCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayString

data class JPccnn(
    override val address: Address,
    override val bytes: DataByteArray,
    val conditionCode: ConditionCode,
    val targetAddress: Address
) : Instruction {
    override fun getTStates(): Int = 10

    override fun execute() {
        if (Registers.registerSet.checkCondition(conditionCode)) {
            Registers.specialPurposeRegisters.setPC(targetAddress.toShort())
        }
    }

    override fun toString(): String = "JP $conditionCode, ${targetAddress.displayString()}"


    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11ccc010 llllllll hhhhhhhh")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val conditionCode = bitPattern.getConditionCode(word, 'c')
            val targetAddress = bitPattern.getUWord(word, 'l', 'h')

            return JPccnn(address, bytes, conditionCode, targetAddress)
        }
    }
}
