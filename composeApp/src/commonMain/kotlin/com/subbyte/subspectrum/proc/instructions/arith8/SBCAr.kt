package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class SBCAr(
    override val address: Address,
    override val bytes: ByteArray,
    val sourceRegister: RegisterCode
) : Instruction {
    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val sourceValue = Registers.registerSet.getRegister(sourceRegister)
        val carryValue = if (Registers.registerSet.getCFlag()) 1 else 0
        
        val a = aRegisterValue.toUByte().toInt()
        val source = sourceValue.toUByte().toInt()
        val diff = a - source - carryValue
        val result = diff.toByte()
        
        Registers.registerSet.setA(result)

        val signFlag = result < 0
        val zeroFlag = result == 0.toByte()
        val halfCarryFlag = ((a and 0x0F) - (source and 0x0F) - carryValue) < 0
        val overflowFlag = ((a xor source) and (a xor diff) and 0x80) != 0
        val carryFlag = diff < 0
        Registers.registerSet.setSFlag(signFlag)
        Registers.registerSet.setZFlag(zeroFlag)
        Registers.registerSet.setHFlag(halfCarryFlag)
        Registers.registerSet.setPVFlag(overflowFlag)
        Registers.registerSet.setNFlag(true)
        Registers.registerSet.setCFlag(carryFlag)
    }

    override fun toString(): String = "SBC A, $sourceRegister"

    companion object : InstructionDefinition {
        override val mCycles: Int = 1
        override val tStates: Int = 4

        override val bitPattern = BitPattern.of("10011rrr")
        override fun decode(word: Long, address: Address): Instruction {
            val r = bitPattern.get(word, 'r')

            val sourceRegister = RegisterCode.entries.first { it.code == r }

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return SBCAr(address, bytes, sourceRegister)
        }
    }
}
