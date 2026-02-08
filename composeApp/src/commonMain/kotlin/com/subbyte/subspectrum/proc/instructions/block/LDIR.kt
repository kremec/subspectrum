package com.subbyte.subspectrum.proc.instructions.block

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class LDIR(
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

        Memory.memorySet.setMemoryCell(deRegisterPairValue.toUShort(), sourceMemoryValue)
        Registers.registerSet.setDE(deRegisterPairValue.inc())
        Registers.registerSet.setHL(hlRegisterPairValue.inc())
        Registers.registerSet.setBC(bcRegisterPairValue.dec())

        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setPVFlag(bcRegisterPairValue.dec() != 0.toShort())
        Registers.registerSet.setNFlag(false)

        conditionBCIs0 = bcRegisterPairValue == 0.toShort()
        if (!conditionBCIs0) {
            Registers.specialPurposeRegisters.setPC(Registers.specialPurposeRegisters.getPC().minus(2).toShort())
            Registers.specialPurposeRegisters.incrementR(2)
        }
    }

    override fun toString(): String = "LDIR"


    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 10110000")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return LDIR(address, bytes)
        }
    }
}
