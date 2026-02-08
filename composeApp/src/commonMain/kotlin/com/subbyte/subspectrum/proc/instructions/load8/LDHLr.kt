package com.subbyte.subspectrum.proc.instructions.load8

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class LDHLr(
    override val address: Address,
    override val bytes: DataByteArray,
    val sourceRegister: RegisterCode
) : Instruction {
    override fun getTStates(): Int = 7

    override fun execute() {
        val sourceValue = Registers.registerSet.getRegister(sourceRegister)
        val hlRegisterPairValue = Registers.registerSet.getHL()
        Memory.memorySet.setMemoryCell(hlRegisterPairValue.toUShort(), sourceValue)
    }

    override fun toString(): String = "LD (HL), $sourceRegister"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("01110rrr")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val sourceRegister = bitPattern.getRegisterCode(word, 'r')

            return LDHLr(address, bytes, sourceRegister)
        }
    }
}
