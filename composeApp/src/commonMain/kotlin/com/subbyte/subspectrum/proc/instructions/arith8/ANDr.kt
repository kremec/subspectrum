package com.subbyte.subspectrum.proc.instructions.arith8

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.experimental.and

data class ANDr(
    override val address: Address,
    override val bytes: DataByteArray,
    val sourceRegister: RegisterCode
) : Instruction {
    override fun getTStates(): Int = 4

    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val sourceValue = Registers.registerSet.getRegister(sourceRegister)
        val result = aRegisterValue.and(sourceValue)
        Registers.registerSet.setA(result)

        val signFlag = result < 0
        val zeroFlag = result == 0.toByte()
        val parityFlag = result.countOneBits() % 2 == 0
        Registers.registerSet.setSFlag(signFlag)
        Registers.registerSet.setZFlag(zeroFlag)
        Registers.registerSet.setHFlag(true)
        Registers.registerSet.setPVFlag(parityFlag)
        Registers.registerSet.setNFlag(false)
        Registers.registerSet.setCFlag(false)
    }

    override fun toString(): String = "AND $sourceRegister"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("10100rrr")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val sourceRegister = bitPattern.getRegisterCode(word, 'r')

            return ANDr(address, bytes, sourceRegister)
        }
    }
}
