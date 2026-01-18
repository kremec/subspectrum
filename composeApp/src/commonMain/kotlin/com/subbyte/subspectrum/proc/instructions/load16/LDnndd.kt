package com.subbyte.subspectrum.proc.instructions.load16

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterPairCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instruction
import com.subbyte.subspectrum.proc.instructions.InstructionDefinition
import com.subbyte.subspectrum.units.Word
import com.subbyte.subspectrum.units.fromBytes
import com.subbyte.subspectrum.units.toBytes

data class LDnndd(
    override val address: Address,
    override val bytes: ByteArray,
    val destinationWord: Word,
    val sourceRegisterPairCode: RegisterPairCode
) : Instruction {
    override fun execute() {
        val sourceValue = Registers.getRegisterPair(sourceRegisterPairCode)
        val (sourceLowValue, sourceHighValue) = sourceValue.toBytes()
        Memory.memorySet.setMemoryCells(destinationWord.toUShort(), byteArrayOf(sourceLowValue, sourceHighValue))
    }

    override fun toString(): String = "LD (${destinationWord.toHexString(HexFormat.UpperCase)}h), $sourceRegisterPairCode"

    companion object : InstructionDefinition {
        override val mCycles: Int = 6
        override val tStates: Int = 20

        override val bitPattern = BitPattern.of("11101101 01dd0011 llllllll hhhhhhhh")
        override fun decode(word: Long, address: Address): Instruction {
            val d = bitPattern.get(word, 'd')
            val l = bitPattern.get(word, 'l').toByte()
            val h = bitPattern.get(word, 'h').toByte()


            val sourceRegisterPair = RegisterPairCode.entries.first { it.code == d }
            val destinationWord = Pair(h, l).fromBytes()

            val bytes = ByteArray(bitPattern.byteCount) { i ->
                val shift = 8 * (bitPattern.byteCount - 1 - i)
                ((word shr shift) and 0xFF).toByte()
            }

            return LDnndd(address, bytes, destinationWord, sourceRegisterPair)
        }
    }
}