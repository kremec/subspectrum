package com.subbyte.subspectrum.proc.instructions.call

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.ConditionCode
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.fromBytes
import com.subbyte.subspectrum.units.toBytes

data class CALLccnn(
    override val address: Address,
    override val bytes: ByteArray,
    val condition: ConditionCode,
    val targetAddress: Address
) : Instruction {
    override fun execute() {
        if (Registers.registerSet.checkCondition(condition)) {
            val pcRegisterValue = Registers.specialPurposeRegisters.getPC()
            val (highByte, lowByte) = pcRegisterValue.toBytes()
            Registers.specialPurposeRegisters.setSP(Registers.specialPurposeRegisters.getSP().minus(2).toShort())
            Memory.memorySet.setMemoryCells(Registers.specialPurposeRegisters.getSP().toUShort(), byteArrayOf(lowByte, highByte))

            Registers.specialPurposeRegisters.setPC(targetAddress.toShort())
        }
    }

    override fun toString(): String = "CALL $condition, ${targetAddress.toString(16).uppercase().padStart(4, '0')}h"

    companion object : InstructionDefinition {
        // TODO: Different timings based on condition
        override val mCycles: Int = 5 // When condition is met
        override val tStates: Int = 17 // When condition is met
        // override val mCycles: Int = 3  // When condition not met
        // override val tStates: Int = 10  // When condition not met


        override val bitPattern = BitPattern.of("11ccc100 llllllll hhhhhhhh")
        override fun decode(word: Long, address: Address): Instruction {
            val c = bitPattern.get(word, 'c')
            val l = bitPattern.get(word, 'l').toByte()
            val h = bitPattern.get(word, 'h').toByte()

            val condition = ConditionCode.entries.first { it.code == c }
            val targetAddress = Pair(h, l).fromBytes().toUShort()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return CALLccnn(address, bytes, condition, targetAddress)
        }
    }
}
