package com.subbyte.subspectrum.proc.instructions.call

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.ConditionCode
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayString
import com.subbyte.subspectrum.units.toBytes

data class CALLccnn(
    override val address: Address,
    override val bytes: DataByteArray,
    val conditionCode: ConditionCode,
    val targetAddress: Address
) : Instruction {
    var conditionMet: Boolean = false

    override fun getTStates(): Int = if (conditionMet) 17 else 10

    override fun execute() {
        conditionMet = Registers.registerSet.checkCondition(conditionCode)
        if (conditionMet) {
            val pcRegisterValue = Registers.specialPurposeRegisters.getPC()
            val (highByte, lowByte) = pcRegisterValue.toBytes()
            Registers.specialPurposeRegisters.setSP(Registers.specialPurposeRegisters.getSP().minus(2).toShort())
            Memory.memorySet.setMemoryCells(
                Registers.specialPurposeRegisters.getSP().toUShort(),
                byteArrayOf(lowByte, highByte)
            )

            Registers.specialPurposeRegisters.setPC(targetAddress.toShort())
        }
    }

    override fun toString(): String = "CALL $conditionCode, ${targetAddress.displayString()}"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11ccc100 llllllll hhhhhhhh")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val conditionCode = bitPattern.getConditionCode(word, 'c')
            val targetAddress = bitPattern.getUWord(word, 'l', 'h')

            return CALLccnn(address, bytes, conditionCode, targetAddress)
        }
    }
}
