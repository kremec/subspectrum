package com.subbyte.subspectrum.proc.instructions.io

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayString

data class OUTnA(
    override val address: Address,
    override val bytes: DataByteArray,
    val destinationByte: UByte
) : Instruction {
    override fun getTStates(): Int = 11

    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        IO.ioPortSet.setIOPort(destinationByte, aRegisterValue)
    }

    override fun toString(): String = "OUT (${destinationByte.displayString()}), A"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11010011 nnnnnnnn")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val destinationByte = bitPattern.getUByte(word, 'n')

            return OUTnA(address, bytes, destinationByte)
        }
    }
}
