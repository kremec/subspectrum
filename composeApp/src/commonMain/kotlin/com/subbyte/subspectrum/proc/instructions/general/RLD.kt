package com.subbyte.subspectrum.proc.instructions.general

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class RLD(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val hlValue = Registers.registerSet.getHL()
        val memoryValue = Memory.memorySet.getMemoryCell(hlValue.toUShort())
        val aValue = Registers.registerSet.getA()

        // Extract nibbles
        val memLowNibble = memoryValue.toInt() and 0x0F  // bits 3-0 of memory
        val memHighNibble = (memoryValue.toInt() shr 4) and 0x0F  // bits 7-4 of memory
        val aLowNibble = aValue.toInt() and 0x0F  // bits 3-0 of A
        val aHighNibble = (aValue.toInt() shr 4) and 0x0F  // bits 7-4 of A (unchanged)

        // Perform rotation: memLow -> memHigh, memHigh -> aLow, aLow -> memLow
        val newMemValue = (memLowNibble shl 4) or aLowNibble  // memLow becomes memHigh, aLow becomes memLow
        val newAValue = (aHighNibble shl 4) or memHighNibble   // aHigh unchanged, memHigh becomes aLow

        // Update memory and accumulator
        Memory.memorySet.setMemoryCell(hlValue.toUShort(), newMemValue.toByte())
        Registers.registerSet.setA(newAValue.toByte())

        // Set flags based on final accumulator value
        val finalAValue = newAValue.toByte()
        Registers.registerSet.setSFlag(finalAValue < 0)
        Registers.registerSet.setZFlag(finalAValue == 0.toByte())
        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setPVFlag(false) // TODO: P/V is set if parity even; otherwise, it is reset
        Registers.registerSet.setNFlag(false)
        // C is not affected
    }

    override fun toString(): String = "RLD"

    companion object : InstructionDefinition {
        override val mCycles: Int = 5
        override val tStates: Int = 18

        override val bitPattern = BitPattern.of("11101101 01101111")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return RLD(address, bytes)
        }
    }
}
