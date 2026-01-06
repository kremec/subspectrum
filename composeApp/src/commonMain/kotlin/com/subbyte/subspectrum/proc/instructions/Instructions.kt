package com.subbyte.subspectrum.proc.instructions

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.proc.instructions.load16.LDHLnn
import com.subbyte.subspectrum.proc.instructions.load16.LDIXMEMnn
import com.subbyte.subspectrum.proc.instructions.load16.LDIXnn
import com.subbyte.subspectrum.proc.instructions.load16.LDIYMEMnn
import com.subbyte.subspectrum.proc.instructions.load16.LDIYnn
import com.subbyte.subspectrum.proc.instructions.load16.LDSPHL
import com.subbyte.subspectrum.proc.instructions.load16.LDSPIX
import com.subbyte.subspectrum.proc.instructions.load16.LDSPIY
import com.subbyte.subspectrum.proc.instructions.load16.LDddMEMnn
import com.subbyte.subspectrum.proc.instructions.load16.LDddnn
import com.subbyte.subspectrum.proc.instructions.load16.LDnnHL
import com.subbyte.subspectrum.proc.instructions.load16.LDnnIX
import com.subbyte.subspectrum.proc.instructions.load16.LDnnIY
import com.subbyte.subspectrum.proc.instructions.load16.LDnndd
import com.subbyte.subspectrum.proc.instructions.load16.POPIX
import com.subbyte.subspectrum.proc.instructions.load16.POPIY
import com.subbyte.subspectrum.proc.instructions.load16.POPqq
import com.subbyte.subspectrum.proc.instructions.load16.PUSHIX
import com.subbyte.subspectrum.proc.instructions.load16.PUSHIY
import com.subbyte.subspectrum.proc.instructions.load16.PUSHqq
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
        // load8
        LDrr,
        LDrn,
        LDrHL,
        LDrIXd,
        LDrIYd,
        LDHLr,
        LDIXdr,
        LDIYdr,
        LDHLn,
        LDIXdn,
        LDIYdn,
        LDABC,
        LDADE,
        LDAnn,
        LDBCA,
        LDDEA,
        LDnnA,
        LDAI,
        LDAR,
        LDIA,
        LDRA,

        // load16
        LDddnn,
        LDIXnn,
        LDIYnn,
        LDHLnn,
        LDddMEMnn,
        LDIXMEMnn,
        LDIYMEMnn,
        LDnnHL,
        LDnndd,
        LDnnIX,
        LDnnIY,
        LDSPHL,
        LDSPIX,
        LDSPIY,
        PUSHqq,
        PUSHIX,
        PUSHIY,
        POPqq,
        POPIX,
        POPIY
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
