package com.subbyte.subspectrum.proc.instructions

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.proc.instructions.load8.LDrr
import com.subbyte.subspectrum.proc.instructions.load8.LDrn
import com.subbyte.subspectrum.proc.instructions.load8.LDrHL
import com.subbyte.subspectrum.proc.instructions.load8.LDrIXd
import com.subbyte.subspectrum.proc.instructions.load8.LDrIYd
import com.subbyte.subspectrum.proc.instructions.load8.LDHLr
import com.subbyte.subspectrum.proc.instructions.load8.LDIXdr
import com.subbyte.subspectrum.proc.instructions.load8.LDIYdr
import com.subbyte.subspectrum.proc.instructions.load8.LDHLn
import com.subbyte.subspectrum.proc.instructions.load8.LDIXdn
import com.subbyte.subspectrum.proc.instructions.load8.LDIYdn
import com.subbyte.subspectrum.proc.instructions.load8.LDABC
import com.subbyte.subspectrum.proc.instructions.load8.LDADE
import com.subbyte.subspectrum.proc.instructions.load8.LDAnn
import com.subbyte.subspectrum.proc.instructions.load8.LDBCA
import com.subbyte.subspectrum.proc.instructions.load8.LDDEA
import com.subbyte.subspectrum.proc.instructions.load8.LDnnA
import com.subbyte.subspectrum.proc.instructions.load8.LDAI
import com.subbyte.subspectrum.proc.instructions.load8.LDAR
import com.subbyte.subspectrum.proc.instructions.load8.LDIA
import com.subbyte.subspectrum.proc.instructions.load8.LDRA

interface Instruction {
    val address: Address
    val bytes: ByteArray

    fun execute()
}

interface InstructionDefinition {
    val mCycles: Int
    val tStates: Int

    val bitPattern: BitPattern
    fun decode(word: Long, address: Address): Instruction
}

object Instructions {
    private val definitions: List<InstructionDefinition> = listOf(
        LDrr.Companion,
        LDrn.Companion,
        LDrHL.Companion,
        LDrIXd.Companion,
        LDrIYd.Companion,
        LDHLr.Companion,
        LDIXdr.Companion,
        LDIYdr.Companion,
        LDHLn.Companion,
        LDIXdn.Companion,
        LDIYdn.Companion,
        LDABC.Companion,
        LDADE.Companion,
        LDAnn.Companion,
        LDBCA.Companion,
        LDDEA.Companion,
        LDnnA.Companion,
        LDAI.Companion,
        LDAR.Companion,
        LDIA.Companion,
        LDRA.Companion,
    )

    fun decode(pc: Address): Instruction {
        val pcInt = pc.toInt()

        for (def in definitions) {
            val pattern = def.bitPattern
            val byteCount = pattern.byteCount

            // Read bytes as long (big-endian)
            var word = 0L
            for (i in 0 until byteCount) {
                val b = Memory.memorySet.getMemoryCell((pcInt + i).toUShort())
                word = (word shl 8) or (b.toLong() and 0xFF)
            }

            if (pattern.matches(word)) {
                return def.decode(word, pc)
            }
        }

        val opcode = Memory.memorySet.getMemoryCell(pc)
        error("Unknown opcode 0x${opcode.toInt() and 0xFF} at 0x${pc.toInt()}")
    }
}
