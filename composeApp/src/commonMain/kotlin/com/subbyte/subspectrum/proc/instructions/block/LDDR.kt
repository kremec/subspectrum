package com.subbyte.subspectrum.proc.instructions.block

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.fromBytes
import com.subbyte.subspectrum.units.toBytes

data class LDDR(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val hlRegisterPairValue = Registers.registerSet.getHL()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(hlRegisterPairValue.toUShort())
        val deRegisterPairValue = Registers.registerSet.getDE()
        val bcRegisterPairValue = Registers.registerSet.getBC()

        Memory.memorySet.setMemoryCell(deRegisterPairValue.toUShort(), sourceMemoryValue)
        Registers.registerSet.setDE(deRegisterPairValue.dec())
        Registers.registerSet.setHL(hlRegisterPairValue.dec())
        Registers.registerSet.setBC(bcRegisterPairValue.dec())

        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setPVFlag(false)
        Registers.registerSet.setNFlag(false)

        if (bcRegisterPairValue != 0.toShort()) {
            val pcRegisterValue = Registers.specialPurposeRegisters.getPC()
            Registers.specialPurposeRegisters.setPC(pcRegisterValue.minus(2).toShort())
        }
        
        // TODO: Interrupts are recognized and two refresh cycles are executed after each data transfer
    }

    override fun toString(): String = "LDIR"

    companion object : InstructionDefinition {
        // TODO: Different timings based on BC != 0 or BC == 0
        override val mCycles: Int = 5
        override val tStates: Int = 21
        // override val mCycles: Int = 4
        // override val tStates: Int = 16

        override val bitPattern = BitPattern.of("11101101 10111000")
        override fun decode(word: Long, address: Address): Instruction {

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return LDDR(address, bytes)
        }
    }
}