package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class SBCAn(
    override val address: Address,
    override val bytes: ByteArray,
    val sourceByte: Byte
) : Instruction {
    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val carryValue = if (Registers.registerSet.getCFlag()) 1 else 0
        val result = aRegisterValue.minus(sourceByte).minus(carryValue).toByte()
        Registers.registerSet.setA(result)

        Registers.registerSet.setSFlag(result < 0)
        Registers.registerSet.setZFlag(result == 0.toByte())
        Registers.registerSet.setHFlag(false) // TODO: H is set if borrow from bit 4; otherwise, it is reset
        Registers.registerSet.setPVFlag(false) // TODO: P/V is set if overflow; otherwise, it is reset
        Registers.registerSet.setNFlag(true)
        Registers.registerSet.setCFlag(false) // TODO: C is set if borrow; otherwise, it is reset
    }

    override fun toString(): String = "SBC A, $sourceByte"

    companion object : InstructionDefinition {
        override val mCycles: Int = 2
        override val tStates: Int = 7

        override val bitPattern = BitPattern.of("11011110 nnnnnnnn")
        override fun decode(word: Long, address: Address): Instruction {
            val n = bitPattern.get(word, 'n')

            val sourceByte = n.toByte()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return SBCAn(address, bytes, sourceByte)
        }
    }
}
