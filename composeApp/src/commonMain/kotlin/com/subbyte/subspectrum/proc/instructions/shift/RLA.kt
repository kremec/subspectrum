package com.subbyte.subspectrum.proc.instructions.shift

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.getBit
import com.subbyte.subspectrum.units.setBit

data class RLA(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 4

    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val oldCarryValue = Registers.registerSet.getCFlag()
        val carryValue = aRegisterValue.getBit(7)
        val result = (aRegisterValue.toInt() shl 1).toByte().setBit(0, oldCarryValue)
        Registers.registerSet.setA(result)

        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setNFlag(false)
        Registers.registerSet.setCFlag(carryValue)
    }

    override fun toString(): String = "RLA"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("00010111")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return RLA(address, bytes)
        }
    }
}
