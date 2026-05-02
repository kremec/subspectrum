package com.subbyte.subspectrum.proc.instructions.block

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.getBit

data class LDDR(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    var conditionBCIs0: Boolean = false

    override fun getTStates(): Int = if (!conditionBCIs0) 21 else 16

    override fun execute() {
        val hlRegisterPairValue = Registers.registerSet.getHL()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(hlRegisterPairValue.toUShort())
        val deRegisterPairValue = Registers.registerSet.getDE()
        val bcRegisterPairValue = Registers.registerSet.getBC()
        val newBC = bcRegisterPairValue.dec()
        val n = (Registers.registerSet.getA().toUByte().toInt() + sourceMemoryValue.toUByte().toInt()).toByte()

        Memory.memorySet.setMemoryCell(deRegisterPairValue.toUShort(), sourceMemoryValue)
        Registers.registerSet.setDE(deRegisterPairValue.dec())
        Registers.registerSet.setHL(hlRegisterPairValue.dec())
        Registers.registerSet.setBC(newBC)

        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setYFFlag(n.getBit(1))
        Registers.registerSet.setXFFlag(n.getBit(3))
        Registers.registerSet.setPVFlag(newBC != 0.toShort())
        Registers.registerSet.setNFlag(false)

        conditionBCIs0 = newBC == 0.toShort()
        if (!conditionBCIs0) {
            val pcRegisterValue = Registers.specialPurposeRegisters.getPC()
            Registers.specialPurposeRegisters.setPC(pcRegisterValue.minus(2).toShort())
            Registers.specialPurposeRegisters.incrementR(2)
        }
    }

    override fun toString(): String = "LDDR"


    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 10111000")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return LDDR(address, bytes)
        }
    }
}
