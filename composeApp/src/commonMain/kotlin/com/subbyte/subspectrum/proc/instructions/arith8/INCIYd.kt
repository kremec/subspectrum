package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class INCIYd(
    override val address: Address,
    override val bytes: ByteArray,
    val displacement: Byte
) : Instruction {
    override fun execute() {
        val iyRegisterValue = Registers.specialPurposeRegisters.getIY()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(iyRegisterValue.plus(displacement).toUShort())
        val result = sourceMemoryValue.inc()
        Memory.memorySet.setMemoryCell(iyRegisterValue.plus(displacement).toUShort(), result)

        Registers.registerSet.setSFlag(result < 0)
        Registers.registerSet.setZFlag(result == 0.toByte())
        Registers.registerSet.setHFlag(false) // TODO: H is set if carry from bit 3; otherwise, it is reset
        Registers.registerSet.setPVFlag(false) // TODO: P/V is set if (IY+d) was 7Fh before operation; otherwise, it is reset
        Registers.registerSet.setNFlag(false)
    }

    override fun toString(): String = "INC (IY + $displacement)"

    companion object : InstructionDefinition {
        override val mCycles: Int = 6
        override val tStates: Int = 23

        override val bitPattern = BitPattern.of("11111101 00110100 dddddddd")
        override fun decode(word: Long, address: Address): Instruction {
            val d = bitPattern.get(word, 'd')

            val displacement = d.toByte()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return INCIYd(address, bytes, displacement)
        }
    }
}