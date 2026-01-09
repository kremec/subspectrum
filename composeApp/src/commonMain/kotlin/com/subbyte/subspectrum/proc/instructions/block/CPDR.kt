package com.subbyte.subspectrum.proc.instructions.block

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.fromBytes
import com.subbyte.subspectrum.units.toBytes

data class CPDR(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val hlRegisterPairValue = Registers.registerSet.getHL()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(hlRegisterPairValue.toUShort())
        val bcRegisterPairValue = Registers.registerSet.getBC()

        val comparison = aRegisterValue.minus(sourceMemoryValue).toByte()

        Registers.registerSet.setHL(hlRegisterPairValue.dec())
        Registers.registerSet.setBC(bcRegisterPairValue.dec())

        Registers.registerSet.setSFlag(comparison < 0.toByte())
        Registers.registerSet.setZFlag(comparison == 0.toByte())
        Registers.registerSet.setHFlag(false) // TODO: H is set if borrow from bit 4; otherwise, it is reset.
        Registers.registerSet.setPVFlag(bcRegisterPairValue.dec() != 0.toShort())
        Registers.registerSet.setNFlag(true)

        if (bcRegisterPairValue != 0.toShort() || comparison == 0.toByte()) {
            Registers.specialPurposeRegisters.setPC(Registers.specialPurposeRegisters.getPC().minus(2).toShort())
        }

        // TODO: Interrupts are recognized and two refresh cycles are executed after each data transfer
    }

    override fun toString(): String = "CPDR"

    companion object : InstructionDefinition {
        // TODO: Different timings based on BC != 0 or BC == 0
        override val mCycles: Int = 5
        override val tStates: Int = 21
        // override val mCycles: Int = 4
        // override val tStates: Int = 16

        override val bitPattern = BitPattern.of("11101101 10111001")
        override fun decode(word: Long, address: Address): Instruction {

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return CPDR(address, bytes)
        }
    }
}