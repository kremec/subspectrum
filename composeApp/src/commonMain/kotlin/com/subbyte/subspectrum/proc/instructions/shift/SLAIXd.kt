package com.subbyte.subspectrum.proc.instructions.shift

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.getBit

data class SLAIXd(
    override val address: Address,
    override val bytes: ByteArray,
    val displacement: Byte
) : Instruction {
    override fun execute() {
        val ixRegisterPairValue = Registers.specialPurposeRegisters.getIX()
        val sourceValue = Memory.memorySet.getMemoryCell(ixRegisterPairValue.plus(displacement).toUShort())
        val carryValue = sourceValue.getBit(7)
        val result = (sourceValue.toInt() shl 1).toByte()
        Memory.memorySet.setMemoryCell(ixRegisterPairValue.plus(displacement).toUShort(), result)

        Registers.registerSet.setSFlag(result < 0)
        Registers.registerSet.setZFlag(result == 0.toByte())
        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setPVFlag(result.countOneBits() % 2 == 0)
        Registers.registerSet.setNFlag(false)
        Registers.registerSet.setCFlag(carryValue)
    }

    override fun toString(): String = "SLA (IX + $displacement)"

    companion object : InstructionDefinition {
        override val mCycles: Int = 6
        override val tStates: Int = 23

        override val bitPattern = BitPattern.of("11011101 11001011 dddddddd 00100110")
        override fun decode(word: Long, address: Address): Instruction {
            val d = bitPattern.get(word, 'd')

            val displacement = d.toByte()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return SLAIXd(address, bytes, displacement)
        }
    }
}
