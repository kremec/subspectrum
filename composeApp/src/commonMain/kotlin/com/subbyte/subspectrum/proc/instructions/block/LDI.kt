package com.subbyte.subspectrum.proc.instructions.block

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.getBit

data class LDI(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 16

    override fun execute() {
        val hlRegisterPairValue = Registers.registerSet.getHL()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(hlRegisterPairValue.toUShort())
        val deRegisterPairValue = Registers.registerSet.getDE()
        val bcRegisterPairValue = Registers.registerSet.getBC()
        val n = (Registers.registerSet.getA().toUByte().toInt() + sourceMemoryValue.toUByte().toInt()).toByte()

        Memory.memorySet.setMemoryCell(deRegisterPairValue.toUShort(), sourceMemoryValue)
        Registers.registerSet.setDE(deRegisterPairValue.inc())
        Registers.registerSet.setHL(hlRegisterPairValue.inc())
        Registers.registerSet.setBC(bcRegisterPairValue.dec())

        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setYFFlag(n.getBit(1))
        Registers.registerSet.setXFFlag(n.getBit(3))
        Registers.registerSet.setPVFlag(bcRegisterPairValue.dec() != 0.toShort())
        Registers.registerSet.setNFlag(false)
    }

    override fun toString(): String = "LDI"


    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 10100000")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return LDI(address, bytes)
        }
    }
}
