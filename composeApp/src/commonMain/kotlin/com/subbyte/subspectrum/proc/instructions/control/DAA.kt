package com.subbyte.subspectrum.proc.instructions.control

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

data class DAA(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA().toInt() and 0xFF
        val isSubtraction = Registers.registerSet.getNFlag()
        val halfCarryFlag = Registers.registerSet.getHFlag()
        val carryFlag = Registers.registerSet.getCFlag()

        val lowerCorrection = halfCarryFlag || ((aRegisterValue and 0x0F) > 9)
        val upperCorrection = carryFlag || (aRegisterValue > 0x99)
        val correction = when {
            lowerCorrection && upperCorrection -> if (isSubtraction) -0x66 else 0x66
            upperCorrection -> if (isSubtraction) -0x60 else 0x60
            lowerCorrection -> if (isSubtraction) -0x06 else 0x06
            else -> 0
        }

        val newHalfCarry = when {
            isSubtraction && !halfCarryFlag -> false
            isSubtraction && halfCarryFlag -> (aRegisterValue and 0x0F) < 6
            else -> (aRegisterValue and 0x0F) >= 0x0A
        }

        val result = (aRegisterValue + correction) and 0xFF
        val resultByte = result.toByte()
        Registers.registerSet.setA(resultByte)

        Registers.registerSet.setSFlag(resultByte < 0)
        Registers.registerSet.setZFlag(resultByte == 0.toByte())
        Registers.registerSet.setHFlag(newHalfCarry)
        Registers.registerSet.setPVFlag(resultByte.countOneBits() % 2 == 0)
        Registers.registerSet.setCFlag(upperCorrection)
    }

    override fun toString(): String = "DAA"

    companion object : InstructionDefinition {
        override val mCycles: Int = 1
        override val tStates: Int = 4

        override val bitPattern = BitPattern.of("00100111")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return DAA(address, bytes)
        }
    }
}