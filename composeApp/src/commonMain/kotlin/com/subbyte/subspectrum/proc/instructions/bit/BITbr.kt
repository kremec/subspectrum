package com.subbyte.subspectrum.proc.instructions.bit

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.getBit

data class BITbr(
    override val address: Address,
    override val bytes: DataByteArray,
    val bitPosition: Int,
    val sourceRegister: RegisterCode
) : Instruction {
    override fun getTStates(): Int = 8

    override fun execute() {
        val sourceValue = Registers.registerSet.getRegister(sourceRegister)
        val bitValue = sourceValue.getBit(bitPosition)

        Registers.registerSet.setZFlag(!bitValue)
        Registers.registerSet.setHFlag(true)
        Registers.registerSet.setNFlag(false)
        // S, P/V unknown
    }

    override fun toString(): String = "BIT $bitPosition, $sourceRegister"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11001011 01bbbrrr")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val bitPosition = bitPattern.getBitPosition(word, 'b')
            val sourceRegister = bitPattern.getRegisterCode(word, 'r')

            return BITbr(address, bytes, bitPosition, sourceRegister)
        }
    }
}
