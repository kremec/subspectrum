package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.units.getBit
import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.base.ULATiming
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition

import com.subbyte.subspectrum.units.DataByteArray

data class LDAR(
    override val address: Address,
    override val bytes: DataByteArray
) : Instruction {
    override fun getTStates(): Int = 9

    override fun execute() {
        val rRegisterValue = Registers.specialPurposeRegisters.getR()
        Registers.registerSet.setA(rRegisterValue)

        Registers.registerSet.setSFlag(rRegisterValue < 0)
        Registers.registerSet.setZFlag(rRegisterValue == 0.toByte())
        Registers.registerSet.setYFFlag((rRegisterValue).getBit(5))
        Registers.registerSet.setXFFlag((rRegisterValue).getBit(3))
        Registers.registerSet.setHFlag(false)
        val willTriggerInterrupt = Processor.IFF1 &&
                ((ULATiming.currentTStatesInFrame + getTStates()) % ULATiming.T_STATES_PER_FRAME == 0)
        Registers.registerSet.setPVFlag(if (willTriggerInterrupt) false else Processor.IFF2)
        Registers.registerSet.setNFlag(false)
    }

    override fun toString(): String = "LD A, R"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101101 01011111")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            return LDAR(address, bytes)
        }
    }
}
