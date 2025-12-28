package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class LDrIYd(
    override val address: Address,
    override val bytes: ByteArray,
    val destinationRegister: RegisterCode,
    val displacement: Byte
) : Instruction {
    override fun execute() {
        val iyRegisterValue = Registers.specialPurposeRegisters.getIY()
        val sourceValue = Memory.memorySet.getMemoryCell(iyRegisterValue.plus(displacement).toUShort())
        Registers.registerSet.setRegister(destinationRegister,  sourceValue)
    }

    override fun toString(): String = "LD $destinationRegister, (IY+${displacement.toHexString(HexFormat.UpperCase)}h)"

    companion object : InstructionDefinition {
        override val mCycles: Int = 5
        override val tStates: Int = 19

        override val bitPattern = BitPattern.of("11011101 01rrr110 dddddddd")
        override fun decode(word: Long, address: Address): Instruction {
            val r = bitPattern.get(word, 'r')
            val d = bitPattern.get(word, 'd')

            val destinationRegister = RegisterCode.entries.first { it.code == r }
            val displacement = d.toByte()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return LDrIYd(address, bytes, destinationRegister, displacement)
        }
    }
}