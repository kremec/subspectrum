package com.subbyte.subspectrum.proc.instructions

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.proc.instructions.arith8.ADCAHL
import com.subbyte.subspectrum.proc.instructions.arith8.ADCAIXd
import com.subbyte.subspectrum.proc.instructions.arith8.ADCAIYd
import com.subbyte.subspectrum.proc.instructions.arith8.ADCAn
import com.subbyte.subspectrum.proc.instructions.arith8.ADCAr
import com.subbyte.subspectrum.proc.instructions.arith8.ADDAHL
import com.subbyte.subspectrum.proc.instructions.arith8.ADDAIXd
import com.subbyte.subspectrum.proc.instructions.arith8.ADDAIYd
import com.subbyte.subspectrum.proc.instructions.arith8.ADDAn
import com.subbyte.subspectrum.proc.instructions.arith8.ADDAr
import com.subbyte.subspectrum.proc.instructions.arith8.ANDHL
import com.subbyte.subspectrum.proc.instructions.arith8.ANDIXd
import com.subbyte.subspectrum.proc.instructions.arith8.ANDIYd
import com.subbyte.subspectrum.proc.instructions.arith8.ANDn
import com.subbyte.subspectrum.proc.instructions.arith8.ANDr
import com.subbyte.subspectrum.proc.instructions.arith8.CPHL
import com.subbyte.subspectrum.proc.instructions.arith8.CPIXd
import com.subbyte.subspectrum.proc.instructions.arith8.CPIYd
import com.subbyte.subspectrum.proc.instructions.arith8.CPn
import com.subbyte.subspectrum.proc.instructions.arith8.CPr
import com.subbyte.subspectrum.proc.instructions.arith8.DECHL
import com.subbyte.subspectrum.proc.instructions.arith8.DECIXd
import com.subbyte.subspectrum.proc.instructions.arith8.DECIYd
import com.subbyte.subspectrum.proc.instructions.arith8.DECr
import com.subbyte.subspectrum.proc.instructions.arith8.INCHL
import com.subbyte.subspectrum.proc.instructions.arith8.INCIXd
import com.subbyte.subspectrum.proc.instructions.arith8.INCIYd
import com.subbyte.subspectrum.proc.instructions.arith8.INCr
import com.subbyte.subspectrum.proc.instructions.arith8.ORHL
import com.subbyte.subspectrum.proc.instructions.arith8.ORIXd
import com.subbyte.subspectrum.proc.instructions.arith8.ORIYd
import com.subbyte.subspectrum.proc.instructions.arith8.ORn
import com.subbyte.subspectrum.proc.instructions.arith8.ORr
import com.subbyte.subspectrum.proc.instructions.arith8.SBCAHL
import com.subbyte.subspectrum.proc.instructions.arith8.SBCAIXd
import com.subbyte.subspectrum.proc.instructions.arith8.SBCAIYd
import com.subbyte.subspectrum.proc.instructions.arith8.SBCAn
import com.subbyte.subspectrum.proc.instructions.arith8.SBCAr
import com.subbyte.subspectrum.proc.instructions.arith8.SUBAHL
import com.subbyte.subspectrum.proc.instructions.arith8.SUBAIXd
import com.subbyte.subspectrum.proc.instructions.arith8.SUBAIYd
import com.subbyte.subspectrum.proc.instructions.arith8.SUBAn
import com.subbyte.subspectrum.proc.instructions.arith8.SUBAr
import com.subbyte.subspectrum.proc.instructions.arith8.XORHL
import com.subbyte.subspectrum.proc.instructions.arith8.XORIXd
import com.subbyte.subspectrum.proc.instructions.arith8.XORIYd
import com.subbyte.subspectrum.proc.instructions.arith8.XORn
import com.subbyte.subspectrum.proc.instructions.arith8.XORr
import com.subbyte.subspectrum.proc.instructions.block.CPD
import com.subbyte.subspectrum.proc.instructions.block.CPDR
import com.subbyte.subspectrum.proc.instructions.block.CPI
import com.subbyte.subspectrum.proc.instructions.block.CPIR
import com.subbyte.subspectrum.proc.instructions.block.LDD
import com.subbyte.subspectrum.proc.instructions.block.LDDR
import com.subbyte.subspectrum.proc.instructions.block.LDI
import com.subbyte.subspectrum.proc.instructions.block.LDIR
import com.subbyte.subspectrum.proc.instructions.ex.EXAFAF
import com.subbyte.subspectrum.proc.instructions.ex.EXDEHL
import com.subbyte.subspectrum.proc.instructions.ex.EXSPHL
import com.subbyte.subspectrum.proc.instructions.ex.EXSPIX
import com.subbyte.subspectrum.proc.instructions.ex.EXSPIY
import com.subbyte.subspectrum.proc.instructions.ex.EXX
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
        POPIY,

        // ex
        EXDEHL,
        EXAFAF,
        EXX,
        EXSPHL,
        EXSPIX,
        EXSPIY,

        // block
        LDI,
        LDIR,
        LDD,
        LDDR,
        CPI,
        CPIR,
        CPD,
        CPDR,

        // arith8
        ADDAr,
        ADDAn,
        ADDAHL,
        ADDAIXd,
        ADDAIYd,
        ADCAr,
        ADCAn,
        ADCAHL,
        ADCAIXd,
        ADCAIYd,
        SUBAr,
        SUBAn,
        SUBAHL,
        SUBAIXd,
        SUBAIYd,
        SBCAr,
        SBCAn,
        SBCAHL,
        SBCAIXd,
        SBCAIYd,
        ANDr,
        ANDn,
        ANDHL,
        ANDIXd,
        ANDIYd,
        ORr,
        ORn,
        ORHL,
        ORIXd,
        ORIYd,
        XORr,
        XORn,
        XORHL,
        XORIXd,
        XORIYd,
        CPr,
        CPn,
        CPHL,
        CPIXd,
        CPIYd,
        INCr,
        INCHL,
        INCIXd,
        INCIYd,
        DECr,
        DECHL,
        DECIXd,
        DECIYd
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
