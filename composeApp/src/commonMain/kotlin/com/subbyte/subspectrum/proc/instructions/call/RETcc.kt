package com.subbyte.subspectrum.proc.instructions.call

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.ConditionCode
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.fromBytes

data class RETcc(
    override val address: Address,
    override val bytes: ByteArray,
    val condition: ConditionCode
) : Instruction {
    override fun execute() {
        if (Registers.registerSet.checkCondition(condition)) {
            val spRegisterValue = Registers.specialPurposeRegisters.getSP()
            val bytes = Memory.memorySet.getMemoryCells(spRegisterValue.toUShort(), spRegisterValue.plus(1).toUShort())
            Registers.specialPurposeRegisters.setSP(spRegisterValue.plus(2).toShort())
            Registers.specialPurposeRegisters.setPC(Pair(bytes[1], bytes[0]).fromBytes())
        }
    }

    override fun toString(): String = "RET $condition"

    companion object : InstructionDefinition {
        // TODO: Different timings based on condition
        override val mCycles: Int = 3 // When condition is met
        override val tStates: Int = 11 // When condition is met
        // override val mCycles: Int = 1  // When condition not met
        // override val tStates: Int = 5  // When condition not met

        override val bitPattern = BitPattern.of("11ccc000")
        override fun decode(word: Long, address: Address): Instruction {
            val c = bitPattern.get(word, 'c')
            
            val condition = ConditionCode.entries.first { it.code == c }

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return RETcc(address, bytes, condition)
        }
    }
}
