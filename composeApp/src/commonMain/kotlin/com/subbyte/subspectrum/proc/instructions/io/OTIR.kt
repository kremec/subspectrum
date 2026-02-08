package com.subbyte.subspectrum.proc.instructions.io

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class OTIR(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    var conditionBIs0: Boolean = false

    override fun getTStates(): Int = if (!conditionBIs0) 21 else 16

    override fun execute() {
        val hlRegisterPairValue = Registers.registerSet.getHL()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(hlRegisterPairValue.toUShort())

        val bRegisterValue = Registers.registerSet.getB()
        val cRegisterValue = Registers.registerSet.getC()
        IO.ioPortSet.setIOPort(cRegisterValue.toUByte(), sourceMemoryValue)

        val newBValue = bRegisterValue.dec()
        Registers.registerSet.setB(newBValue)
        Registers.registerSet.setHL(hlRegisterPairValue.inc())

        Registers.registerSet.setZFlag(true)
        Registers.registerSet.setNFlag(true)

        conditionBIs0 = newBValue == 0.toByte()
        if (!conditionBIs0) {
            Registers.specialPurposeRegisters.setPC(Registers.specialPurposeRegisters.getPC().minus(2).toShort())
            Registers.specialPurposeRegisters.incrementR(2)
        }
    }

    override fun toString(): String = "OTIR"


    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 10110011")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return OTIR(address, bytes)
        }
    }
}
