package com.subbyte.subspectrum.proc.instructions.block

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray
data class CPI(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 16

    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val hlRegisterPairValue = Registers.registerSet.getHL()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(hlRegisterPairValue.toUShort())
        val bcRegisterPairValue = Registers.registerSet.getBC()

        val comparison = aRegisterValue.minus(sourceMemoryValue).toByte()

        Registers.registerSet.setHL(hlRegisterPairValue.inc())
        Registers.registerSet.setBC(bcRegisterPairValue.dec())

        Registers.registerSet.setSFlag(comparison < 0.toByte())
        Registers.registerSet.setZFlag(comparison == 0.toByte())
        Registers.registerSet.setHFlag(((aRegisterValue.toUByte().toInt() and 0x0F) - (sourceMemoryValue.toUByte().toInt() and 0x0F)) < 0)
        Registers.registerSet.setPVFlag(bcRegisterPairValue.dec() != 0.toShort())
        Registers.registerSet.setNFlag(true)
    }

    override fun toString(): String = "CPI"


    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 10100001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return CPI(address, bytes)
        }
    }
}
