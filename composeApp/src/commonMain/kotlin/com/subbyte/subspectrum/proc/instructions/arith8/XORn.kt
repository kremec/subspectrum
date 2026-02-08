package com.subbyte.subspectrum.proc.instructions.arith8

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.displayString
import kotlin.experimental.xor

data class XORn(
    override val address: Address,
    override val bytes: DataByteArray,
    val sourceUByte: UByte
) : Instruction {
    override fun getTStates(): Int = 7

    override fun execute() {
        val aRegisterValue = Registers.registerSet.getA()
        val result = aRegisterValue.xor(sourceUByte.toByte())
        Registers.registerSet.setA(result)

        val signFlag = result < 0
        val zeroFlag = result == 0.toByte()
        val parityFlag = result.countOneBits() % 2 == 0
        Registers.registerSet.setSFlag(signFlag)
        Registers.registerSet.setZFlag(zeroFlag)
        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setPVFlag(parityFlag)
        Registers.registerSet.setNFlag(false)
        Registers.registerSet.setCFlag(false)
    }

    override fun toString(): String = "XOR ${sourceUByte.displayString()}"

    companion object : InstructionDefinition {
        override val bitPattern = BitPattern.of("11101110 nnnnnnnn")
        override fun decode(word: Long, address: Address): Instruction {
            val bytes = bitPattern.toInstructionByteArray(word)

            val sourceUByte = bitPattern.getUByte(word, 'n')

            return XORn(address, bytes, sourceUByte)
        }
    }
}
