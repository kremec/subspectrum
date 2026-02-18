package com.subbyte.subspectrum.proc.instructions.io

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray

data class OUTCr(
    override val address: Address,
    override val bytes: DataByteArray,
    val sourceRegister: RegisterCode
) : Instruction {
    override fun getTStates(): Int = 12

    override fun execute() {
        val sourceRegisterValue = Registers.registerSet.getRegister(sourceRegister)
        val destinationIOPortAddress = Registers.registerSet.getBC().toUShort()
        IO.ioPortSet.setIO(destinationIOPortAddress, sourceRegisterValue)
    }

    override fun toString(): String = "OUT (C), $sourceRegister"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 01rrr001")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val sourceRegister = bitPattern.getRegisterCode(word, 'r')

            return OUTCr(address, bytes, sourceRegister)
        }
    }
}
