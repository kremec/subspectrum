package com.subbyte.subspectrum.proc.instructions.undocumented.ed

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class NEG_7(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 8

    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val result = (-aRegisterValue).toByte()
        Registers.registerSet.setA(result)

        Registers.registerSet.setSFlag(result < 0)
        Registers.registerSet.setZFlag(result == 0.toByte())
        Registers.registerSet.setHFlag((aRegisterValue.toUByte().toInt() and 0x0F) != 0)
        Registers.registerSet.setPVFlag(aRegisterValue == 0x80.toByte())
        Registers.registerSet.setNFlag(true)
        Registers.registerSet.setCFlag(aRegisterValue != 0.toByte())
    }

    override fun toString(): String = "NEG"

    companion object Companion : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 01111100")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return NEG_7(address, bytes)
        }
    }
}
