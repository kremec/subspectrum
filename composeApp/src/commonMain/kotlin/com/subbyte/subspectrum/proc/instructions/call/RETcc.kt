package com.subbyte.subspectrum.proc.instructions.call

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.ConditionCode
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.wordFromBytes

data class RETcc(
    override val address: Address,
    override val bytes: DataByteArray,
    val conditionCode: ConditionCode
) : Instruction {
    private var conditionMet: Boolean = false

    override fun getTStates(): Int = if (conditionMet) 11 else 5

    override fun execute() {
        conditionMet = Registers.registerSet.checkCondition(conditionCode)
        if (conditionMet) {
            val spRegisterValue = Registers.specialPurposeRegisters.getSP()
            val bytes = Memory.memorySet.getMemoryCells(spRegisterValue.toUShort(), spRegisterValue.plus(1).toUShort())
            Registers.specialPurposeRegisters.setSP(spRegisterValue.plus(2).toShort())
            Registers.specialPurposeRegisters.setPC(Pair(bytes[1], bytes[0]).wordFromBytes())
        }
    }

    override fun toString(): String = "RET $conditionCode"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11ccc000")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val conditionCode = bitPattern.getConditionCode(word, 'c')

            return RETcc(address, bytes, conditionCode)
        }
    }
}
