package com.subbyte.subspectrum.proc.instructions.call

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.fromBytes

data class RETI(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val spRegisterValue = Registers.specialPurposeRegisters.getSP()
        val bytes = Memory.memorySet.getMemoryCells(spRegisterValue.toUShort(), spRegisterValue.plus(1).toUShort())
        Registers.specialPurposeRegisters.setSP(spRegisterValue.plus(2).toShort())
        Registers.specialPurposeRegisters.setPC(Pair(bytes[1], bytes[0]).fromBytes())
        
        // TODO: Signal I/O device that interrupt routine is completed
        // TODO: Reset IEO (Interrupt Enable Out) for daisy-chain interrupt handling
    }

    override fun toString(): String = "RETI"

    companion object : InstructionDefinition {
        override val mCycles: Int = 4
        override val tStates: Int = 14

        override val bitPattern = BitPattern.of("11101101 01001101")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return RETI(address, bytes)
        }
    }
}
