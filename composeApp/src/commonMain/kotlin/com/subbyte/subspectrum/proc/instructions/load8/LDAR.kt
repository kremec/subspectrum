package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class LDAR(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val rRegisterValue = Registers.specialPurposeRegisters.getR()
        Registers.registerSet.setA(rRegisterValue)

        Registers.registerSet.setSFlag(rRegisterValue < 0)
        Registers.registerSet.setZFlag(rRegisterValue == 0.toByte())
        Registers.registerSet.setHFlag(false)
        // TODO: P/V contains contents of IFF2
        Registers.registerSet.setNFlag(false)
        // TODO: If an interrupt occurs during execution of this instruction, the Parity flag contains a 0
    }

    override fun toString(): String = "LD A, R"

    companion object : InstructionDefinition {
        override val mCycles: Int = 2
        override val tStates: Int = 9

        override val bitPattern = BitPattern.of("11101101 01011111")
        override fun decode(word: Long, address: Address): Instruction {

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return LDAR(address, bytes)
        }
    }
}