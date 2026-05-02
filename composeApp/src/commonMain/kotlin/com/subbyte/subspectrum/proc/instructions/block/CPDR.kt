package com.subbyte.subspectrum.proc.instructions.block

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.getBit

data class CPDR(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    var conditionBCIs0: Boolean = false
    var conditionMemHLIsA: Boolean = false

    override fun getTStates(): Int = if (!conditionBCIs0 && !conditionMemHLIsA) 21 else 16

    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val hlRegisterPairValue = Registers.registerSet.getHL()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(hlRegisterPairValue.toUShort())
        val bcRegisterPairValue = Registers.registerSet.getBC()

        val newHL = hlRegisterPairValue.dec()
        val newBC = bcRegisterPairValue.dec()

        val diff = aRegisterValue.toUByte().toInt() - sourceMemoryValue.toUByte().toInt()
        val comparison = diff.toByte()
        val halfCarryFlag = (aRegisterValue.toUByte().toInt() % 16) < (sourceMemoryValue.toUByte().toInt() % 16)
        val n = (diff - (if (halfCarryFlag) 1 else 0)).toByte()

        Registers.registerSet.setHL(newHL)
        Registers.registerSet.setBC(newBC)

        Registers.registerSet.setSFlag(comparison < 0.toByte())
        Registers.registerSet.setZFlag(comparison == 0.toByte())
        Registers.registerSet.setYFFlag(n.getBit(1))
        Registers.registerSet.setHFlag(halfCarryFlag)
        Registers.registerSet.setXFFlag(n.getBit(3))
        Registers.registerSet.setPVFlag(newBC != 0.toShort())
        Registers.registerSet.setNFlag(true)

        conditionBCIs0 = newBC == 0.toShort()
        conditionMemHLIsA = comparison == 0.toByte()
        if (!conditionBCIs0 && !conditionMemHLIsA) {
            Registers.specialPurposeRegisters.setPC(Registers.specialPurposeRegisters.getPC().minus(2).toShort())
            Registers.specialPurposeRegisters.incrementR(2)
        }
    }

    override fun toString(): String = "CPDR"


    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 10111001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return CPDR(address, bytes)
        }
    }
}
