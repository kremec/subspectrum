package com.subbyte.subspectrum.proc.instructions.io

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.getBit

data class INIR(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    var conditionBIs0: Boolean = false

    override fun getTStates(): Int = if (!conditionBIs0) 21 else 16

    override fun execute() {
        val bRegisterValue = Registers.registerSet.getB()
        val hlRegisterPairValue = Registers.registerSet.getHL()

        val newBValue = bRegisterValue.dec()
        Registers.registerSet.setB(newBValue)
        val inputPortAddress = Registers.registerSet.getBC().toUShort()
        val inputData = IO.ioPortSet.getIO(inputPortAddress)

        Memory.memorySet.setMemoryCell(hlRegisterPairValue.toUShort(), inputData)
        Registers.registerSet.setHL(hlRegisterPairValue.inc())

        val k = inputData.toUByte().toInt() + Registers.registerSet.getC().inc().toUByte().toInt()
        val carry = k > 0xFF
        val parityValue = (k % 8) xor newBValue.toUByte().toInt()

        Registers.registerSet.setSFlag(newBValue < 0)
        Registers.registerSet.setZFlag(newBValue == 0.toByte())
        Registers.registerSet.setYFFlag(newBValue.getBit(5))
        Registers.registerSet.setXFFlag(newBValue.getBit(3))
        Registers.registerSet.setHFlag(carry)
        Registers.registerSet.setPVFlag(parityValue.countOneBits() % 2 == 0)
        Registers.registerSet.setNFlag(inputData < 0)
        Registers.registerSet.setCFlag(carry)

        conditionBIs0 = newBValue == 0.toByte()
        if (!conditionBIs0) {
            Registers.specialPurposeRegisters.setPC(Registers.specialPurposeRegisters.getPC().minus(2).toShort())
            Registers.specialPurposeRegisters.incrementR(2)
        }
    }

    override fun toString(): String = "INIR"


    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 10110010")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return INIR(address, bytes)
        }
    }
}
