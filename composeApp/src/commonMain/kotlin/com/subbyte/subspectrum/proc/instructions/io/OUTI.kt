package com.subbyte.subspectrum.proc.instructions.io

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray
data class OUTI(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 16

    override fun execute() {
        val hlRegisterPairValue = Registers.registerSet.getHL()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(hlRegisterPairValue.toUShort())

        val bRegisterValue = Registers.registerSet.getB()
        val cRegisterValue = Registers.registerSet.getC()
        IO.ioPortSet.setIOPort(cRegisterValue.toUByte(), sourceMemoryValue)

        val newBValue = bRegisterValue.dec()
        Registers.registerSet.setB(newBValue)
        Registers.registerSet.setHL(hlRegisterPairValue.inc())

        Registers.registerSet.setZFlag(newBValue == 0.toByte())
        Registers.registerSet.setNFlag(true)
    }

    override fun toString(): String = "OUTI"


    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 10100011")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return OUTI(address, bytes)
        }
    }
}
