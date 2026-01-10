package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class ADCAr(
    override val address: Address,
    override val bytes: ByteArray,
    val sourceRegister: RegisterCode
) : Instruction {
    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val sourceValue = Registers.registerSet.getRegister(sourceRegister)
        val carryValue = if (Registers.registerSet.getCFlag()) 1 else 0
        val result = aRegisterValue.plus(sourceValue).plus(carryValue).toByte()
        Registers.registerSet.setA(result)

        Registers.registerSet.setSFlag(result < 0)
        Registers.registerSet.setZFlag(result == 0.toByte())
        Registers.registerSet.setHFlag(false) // TODO: H is set if carry from bit 3; otherwise, it is reset
        Registers.registerSet.setPVFlag(false) // TODO: P/V is set if overflow; otherwise, it is reset
        Registers.registerSet.setNFlag(false)
        Registers.registerSet.setCFlag(false) // TODO: C is set if carry from bit 7; otherwise, it is reset
    }

    override fun toString(): String = "ADC A, $sourceRegister"

    companion object : InstructionDefinition {
        override val mCycles: Int = 1
        override val tStates: Int = 4

        override val bitPattern = BitPattern.of("10001rrr")
        override fun decode(word: Long, address: Address): Instruction {
            val r = bitPattern.get(word, 'r')

            val sourceRegister = RegisterCode.entries.first { it.code == r }

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return ADCAr(address, bytes, sourceRegister)
        }
    }
}