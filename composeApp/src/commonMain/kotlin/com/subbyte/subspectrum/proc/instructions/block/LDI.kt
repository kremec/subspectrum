package com.subbyte.subspectrum.proc.instructions.block

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.fromBytes
import com.subbyte.subspectrum.units.toBytes

data class LDI(
    override val address: Address,
    override val bytes: ByteArray
) : Instruction {
    override fun execute() {
        val hlRegisterPairValue = Registers.registerSet.getHL()
        val sourceMemoryValue = Memory.memorySet.getMemoryCell(hlRegisterPairValue.toUShort())
        val deRegisterPairValue = Registers.registerSet.getDE()
        val bcRegisterPairValue = Registers.registerSet.getBC()

        Memory.memorySet.setMemoryCell(deRegisterPairValue.toUShort(), sourceMemoryValue)
        Registers.registerSet.setDE(deRegisterPairValue.inc())
        Registers.registerSet.setHL(hlRegisterPairValue.inc())
        Registers.registerSet.setBC(bcRegisterPairValue.dec())

        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setPVFlag(bcRegisterPairValue.dec() != 0.toShort())
        Registers.registerSet.setNFlag(false)
    }

    override fun toString(): String = "LDI"

    companion object : InstructionDefinition {
        override val mCycles: Int = 4
        override val tStates: Int = 16

        override val bitPattern = BitPattern.of("11101101 10100000")
        override fun decode(word: Long, address: Address): Instruction {

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return LDI(address, bytes)
        }
    }
}