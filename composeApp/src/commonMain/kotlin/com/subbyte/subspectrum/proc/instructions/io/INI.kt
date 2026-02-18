package com.subbyte.subspectrum.proc.instructions.io

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray

data class INI(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 16

    override fun execute() {
        val bRegisterValue = Registers.registerSet.getB()
        val inputPortAddress = Registers.registerSet.getBC().toUShort()
        val inputData = IO.ioPortSet.getIO(inputPortAddress)

        val hlRegisterPairValue = Registers.registerSet.getHL()
        Memory.memorySet.setMemoryCell(hlRegisterPairValue.toUShort(), inputData)

        val newBValue = bRegisterValue.dec()
        Registers.registerSet.setB(newBValue)
        Registers.registerSet.setHL(hlRegisterPairValue.inc())

        Registers.registerSet.setZFlag(newBValue == 0.toByte())
        Registers.registerSet.setNFlag(true)
    }

    override fun toString(): String = "INI"


    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 10100010")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return INI(address, bytes)
        }
    }
}
