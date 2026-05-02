package com.subbyte.subspectrum.proc.instructions.block

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.getBit

data class CPD(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 16

    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val hlRegisterPairValue = Registers.registerSet.getHL()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(hlRegisterPairValue.toUShort())
        val bcRegisterPairValue = Registers.registerSet.getBC()

        val diff = aRegisterValue.toUByte().toInt() - sourceMemoryValue.toUByte().toInt()
        val comparison = diff.toByte()
        val halfCarryFlag = (aRegisterValue.toUByte().toInt() % 16) < (sourceMemoryValue.toUByte().toInt() % 16)
        val n = (diff - (if (halfCarryFlag) 1 else 0)).toByte()

        Registers.registerSet.setHL(hlRegisterPairValue.dec())
        Registers.registerSet.setBC(bcRegisterPairValue.dec())

        Registers.registerSet.setSFlag(comparison < 0.toByte())
        Registers.registerSet.setZFlag(comparison == 0.toByte())
        Registers.registerSet.setYFFlag(n.getBit(1))
        Registers.registerSet.setHFlag(halfCarryFlag)
        Registers.registerSet.setXFFlag(n.getBit(3))
        Registers.registerSet.setPVFlag(bcRegisterPairValue.dec() != 0.toShort())
        Registers.registerSet.setNFlag(true)
    }

    override fun toString(): String = "CPD"


    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 10101001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return CPD(address, bytes)
        }
    }
}
