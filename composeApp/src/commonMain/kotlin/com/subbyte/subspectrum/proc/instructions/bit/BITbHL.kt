package com.subbyte.subspectrum.proc.instructions.bit

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.getBit
import com.subbyte.subspectrum.units.toBytes

data class BITbHL(
    override val address: Address,
    override val bytes: DataByteArray,
    val bitPosition: Int
) : Instruction {
    override fun getTStates(): Int = 12

    override fun execute() {
        val hlValue = Registers.registerSet.getHL()
        val memoryValue = Memory.memorySet.getMemoryCell(hlValue.toUShort())
        val bitValue = memoryValue.getBit(bitPosition)
        val memptrHigh = Registers.specialPurposeRegisters.getMEMPTR().toBytes().first

        Registers.registerSet.setSFlag(bitPosition == 7 && bitValue)
        Registers.registerSet.setZFlag(!bitValue)
        Registers.registerSet.setYFFlag(memptrHigh.getBit(5))
        Registers.registerSet.setXFFlag(memptrHigh.getBit(3))
        Registers.registerSet.setHFlag(true)
        Registers.registerSet.setPVFlag(!bitValue)
        Registers.registerSet.setNFlag(false)
    }

    override fun toString(): String = "BIT $bitPosition, (HL)"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11001011 01bbb110")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val bitPosition = bitPattern.getBitPosition(word, 'b')

            return BITbHL(address, bytes, bitPosition)
        }
    }
}
