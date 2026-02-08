package com.subbyte.subspectrum.proc.instructions.io

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayString

data class INAn(
    override val address: Address,
    override val bytes: DataByteArray,
    val sourceUByte: UByte
) : Instruction {
    override fun getTStates(): Int = 11

    override fun execute() {
        val sourceIOPortValue = IO.ioPortSet.getIOPort(sourceUByte)
        Registers.registerSet.setA(sourceIOPortValue)
    }

    override fun toString(): String = "IN A, (${sourceUByte.displayString()})"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11011011 nnnnnnnn")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val sourceUByte = bitPattern.getUByte(word, 'n')

            return INAn(address, bytes, sourceUByte)
        }
    }
}
