package com.subbyte.subspectrum.proc.instructions.shift

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.getBit

data class SRLr(
    override val address: Address,
    override val bytes: ByteArray,
    val sourceRegister: RegisterCode
) : Instruction {
    override fun execute() {
        val sourceRegisterValue = Registers.registerSet.getRegister(sourceRegister)
        val carryValue = sourceRegisterValue.getBit(0)
        val result = ((sourceRegisterValue.toInt() and 0xFF) ushr 1).toByte()
        Registers.registerSet.setRegister(sourceRegister, result)

        Registers.registerSet.setSFlag(false)
        Registers.registerSet.setZFlag(result == 0.toByte())
        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setPVFlag(result.countOneBits() % 2 == 0)
        Registers.registerSet.setNFlag(false)
        Registers.registerSet.setCFlag(carryValue)
    }

    override fun toString(): String = "SRL $sourceRegister"

    companion object : InstructionDefinition {
        override val mCycles: Int = 2
        override val tStates: Int = 8

        override val bitPattern = BitPattern.of("11001011 00111rrr")
        override fun decode(word: Long, address: Address): Instruction {
            val r = bitPattern.get(word, 'r')

            val sourceRegister = RegisterCode.entries.first { it.code == r }

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return SRLr(address, bytes, sourceRegister)
        }
    }
}
