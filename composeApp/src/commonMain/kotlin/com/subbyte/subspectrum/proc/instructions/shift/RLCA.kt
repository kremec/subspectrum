package com.subbyte.subspectrum.proc.instructions.shift

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.getBit

data class RLCA(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 4

    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val carryValue = aRegisterValue.getBit(7)
        val shifted = aRegisterValue.toInt() and 0xFF
        val result = ((shifted shl 1) or (if (carryValue) 0x01 else 0x00)).toByte()
        Registers.registerSet.setA(result)

        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setNFlag(false)
        Registers.registerSet.setCFlag(carryValue)
    }

    override fun toString(): String = "RLCA"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("00000111")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return RLCA(address, bytes)
        }
    }
}
