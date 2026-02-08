package com.subbyte.subspectrum.proc.instructions.control

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.experimental.inv

data class CPL(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 4

    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val result = aRegisterValue.inv()
        Registers.registerSet.setA(result)

        Registers.registerSet.setHFlag(true)
        Registers.registerSet.setNFlag(true)
    }

    override fun toString(): String = "CPL"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("00101111")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return CPL(address, bytes)
        }
    }
}
