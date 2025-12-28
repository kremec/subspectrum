package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class LDHLr(
    override val address: Address,
    override val bytes: ByteArray,
    val sourceRegister: RegisterCode
) : Instruction {
    override fun execute() {
        val sourceValue = Registers.registerSet.getRegister(sourceRegister)
        val hlRegisterValue = Registers.registerSet.getHL()
        Memory.memorySet.setMemoryCell(hlRegisterValue.toUShort(), sourceValue)
    }

    override fun toString(): String = "LD (HL), $sourceRegister"

    companion object : InstructionDefinition {
        override val mCycles: Int = 2
        override val tStates: Int = 7

        override val bitPattern = BitPattern.of("01110rrr")
        override fun decode(word: Long, address: Address): Instruction {
            val r = bitPattern.get(word, 'r')

            val sourceRegister = RegisterCode.entries.first { it.code == r }

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return LDHLr(address, bytes, sourceRegister)
        }
    }
}