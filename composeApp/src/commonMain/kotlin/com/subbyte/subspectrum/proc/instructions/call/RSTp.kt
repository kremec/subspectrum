package com.subbyte.subspectrum.proc.instructions.call

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.toBytes

data class RSTp(
    override val address: Address,
    override val bytes: ByteArray,
    val restartAddress: UByte
) : Instruction {
    override fun execute() {
        val pcRegisterPairValue = Registers.specialPurposeRegisters.getPC()
        val (highByte, lowByte) = pcRegisterPairValue.toBytes()
        Registers.specialPurposeRegisters.setSP(Registers.specialPurposeRegisters.getSP().minus(2).toShort())
        Memory.memorySet.setMemoryCells(Registers.specialPurposeRegisters.getSP().toUShort(), byteArrayOf(lowByte, highByte))

        Registers.specialPurposeRegisters.setPC(restartAddress.toShort())
    }

    override fun toString(): String = "RST ${restartAddress.toString(16).uppercase().padStart(2, '0')}h"

    companion object : InstructionDefinition {
        override val mCycles: Int = 3
        override val tStates: Int = 11

        override val bitPattern = BitPattern.of("11ttt111")
        override fun decode(word: Long, address: Address): Instruction {
            val t = bitPattern.get(word, 't')
            
            val restartAddress = (t shl 3).toUByte()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return RSTp(address, bytes, restartAddress)
        }
    }
}
