package com.subbyte.subspectrum.proc.instructions.shift

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.getBit

data class SRAHL(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 15

    override fun execute() {
        val hlRegisterPairValue = Registers.registerSet.getHL()
        val sourceValue = Memory.memorySet.getMemoryCell(hlRegisterPairValue.toUShort())
        val carryValue = sourceValue.getBit(0)
        val result = (sourceValue.toInt() shr 1).toByte()
        Memory.memorySet.setMemoryCell(hlRegisterPairValue.toUShort(), result)

        Registers.registerSet.setSFlag(result < 0)
        Registers.registerSet.setZFlag(result == 0.toByte())
        Registers.registerSet.setYFFlag((result).getBit(5))
        Registers.registerSet.setXFFlag((result).getBit(3))
        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setPVFlag(result.countOneBits() % 2 == 0)
        Registers.registerSet.setNFlag(false)
        Registers.registerSet.setCFlag(carryValue)
    }

    override fun toString(): String = "SRA (HL)"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11001011 00101110")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return SRAHL(address, bytes)
        }
    }
}
