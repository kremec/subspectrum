package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class LDIYdr(
    override val address: Address,
    override val bytes: ByteArray,
    val sourceRegister: RegisterCode,
    val displacement: Byte
) : Instruction {
    override fun execute() {
        val sourceValue = Registers.registerSet.getRegister(sourceRegister)
        val iyRegisterValue = Registers.specialPurposeRegisters.getIY()
        Memory.memorySet.setMemoryCell(iyRegisterValue.plus(displacement).toUShort(), sourceValue)
    }

    override fun toString(): String = "LD (IY+${displacement.toHexString(HexFormat.UpperCase)}h), $sourceRegister"

    companion object : InstructionDefinition {
        override val mCycles: Int = 5
        override val tStates: Int = 19

        override val bitPattern = BitPattern.of("11111101 01110rrr dddddddd")
        override fun decode(word: Long, address: Address): Instruction {
            val r = bitPattern.get(word, 'r')
            val d = bitPattern.get(word, 'd')

            val sourceRegister = RegisterCode.entries.first { it.code == r }
            val displacement = d.toByte()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return LDIYdr(address, bytes, sourceRegister, displacement)
        }
    }
}