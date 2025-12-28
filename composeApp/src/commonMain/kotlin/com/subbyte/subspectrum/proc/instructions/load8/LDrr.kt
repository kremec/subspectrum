package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class LDrr(
    override val address: Address,
    override val bytes: ByteArray,
    val destinationRegister: RegisterCode,
    val sourceRegister: RegisterCode
) : Instruction {
    override fun execute() {
        val sourceValue = Registers.registerSet.getRegister(sourceRegister)
        Registers.registerSet.setRegister(destinationRegister, sourceValue)
    }

    override fun toString(): String = "LD $destinationRegister, $sourceRegister"

    companion object : InstructionDefinition {
        override val mCycles: Int = 1
        override val tStates: Int = 4

        override val bitPattern = BitPattern.of("01xxxyyy")
        override fun decode(word: Long, address: Address): Instruction {
            val x = bitPattern.get(word, 'x')
            val y = bitPattern.get(word, 'y')

            val destinationRegister = RegisterCode.entries.first { it.code == x }
            val sourceRegister = RegisterCode.entries.first { it.code == y }

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return LDrr(address, bytes, destinationRegister, sourceRegister)
        }
    }
}