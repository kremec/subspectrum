package com.subbyte.subspectrum.proc.instructions.undocumented.ed

import com.subbyte.subspectrum.units.getBit
import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray

data class INFC(
    override val address: Address,
    override val bytes: DataByteArray,
) : Instruction {
    override fun getTStates(): Int = 12

    override fun execute() {
        val sourceIOPortAddress = Registers.registerSet.getBC().toUShort()
        val sourceIOPortValue = IO.ioPortSet.getIO(sourceIOPortAddress)

        Registers.registerSet.setSFlag(sourceIOPortValue < 0)
        Registers.registerSet.setZFlag(sourceIOPortValue == 0.toByte())
        Registers.registerSet.setYFFlag((sourceIOPortValue).getBit(5))
        Registers.registerSet.setXFFlag((sourceIOPortValue).getBit(3))
        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setPVFlag(sourceIOPortValue.countOneBits() % 2 == 0)
        Registers.registerSet.setNFlag(false)
    }

    override fun toString(): String = "IN F, (C)"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 01110000")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return INFC(address, bytes)
        }
    }
}
