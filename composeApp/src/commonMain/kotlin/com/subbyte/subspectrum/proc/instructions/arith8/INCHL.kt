package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class INCHL(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val hlRegisterValue = Registers.registerSet.getHL()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(hlRegisterValue.toUShort())
        
        val source = sourceMemoryValue.toUByte().toInt()
        val sum = source + 1
        val result = sum.toByte()
        
        Memory.memorySet.setMemoryCell(hlRegisterValue.toUShort(), result)

        val signFlag = result < 0
        val zeroFlag = result == 0.toByte()
        val halfCarryFlag = (source and 0x0F) == 0x0F
        val overflowFlag = source == 0x7F
        Registers.registerSet.setSFlag(signFlag)
        Registers.registerSet.setZFlag(zeroFlag)
        Registers.registerSet.setHFlag(halfCarryFlag)
        Registers.registerSet.setPVFlag(overflowFlag)
        Registers.registerSet.setNFlag(false)
    }

    override fun toString(): String = "INC (HL)"

    companion object : InstructionDefinition {
        override val mCycles: Int = 3
        override val tStates: Int = 11

        override val bitPattern = BitPattern.of("00110100")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return INCHL(address, bytes)
        }
    }
}