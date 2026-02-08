package com.subbyte.subspectrum.proc.instructions.io

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class INrC(
    override val address: Address,
    override val bytes: DataByteArray,
    val destinationRegister: RegisterCode
) : Instruction {
    override fun getTStates(): Int = 12

    override fun execute() {
        val cRegisterValue = Registers.registerSet.getC()
        val sourceIOPortValue = IO.ioPortSet.getIOPort(cRegisterValue.toUByte())
        Registers.registerSet.setRegister(destinationRegister, sourceIOPortValue)

        Registers.registerSet.setSFlag(sourceIOPortValue < 0)
        Registers.registerSet.setZFlag(sourceIOPortValue == 0.toByte())
        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setPVFlag(sourceIOPortValue.countOneBits() % 2 == 0)
        Registers.registerSet.setNFlag(false)
    }

    override fun toString(): String = "IN $destinationRegister, (C)"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 01rrr000")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val destinationRegister = bitPattern.getRegisterCode(word, 'r')

            return INrC(address, bytes, destinationRegister)
        }
    }
}
