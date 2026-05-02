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

data class IND(
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
        Registers.registerSet.setHL(hlRegisterPairValue.dec())

        val k = inputData.toUByte().toInt() + Registers.registerSet.getC().dec().toUByte().toInt()
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
    }

    override fun toString(): String = "IND"


    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 10101010")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return IND(address, bytes)
        }
    }
}
