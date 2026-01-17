package com.subbyte.subspectrum.proc.instructions

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.proc.instructions.arith16.ADCHLss
import com.subbyte.subspectrum.proc.instructions.arith16.ADDHLss
import com.subbyte.subspectrum.proc.instructions.arith16.ADDIXss
import com.subbyte.subspectrum.proc.instructions.arith16.ADDIYss
import com.subbyte.subspectrum.proc.instructions.arith16.DECIX
import com.subbyte.subspectrum.proc.instructions.arith16.DECIY
import com.subbyte.subspectrum.proc.instructions.arith16.DECss
import com.subbyte.subspectrum.proc.instructions.arith16.INCIX
import com.subbyte.subspectrum.proc.instructions.arith16.INCIY
import com.subbyte.subspectrum.proc.instructions.arith16.INCss
import com.subbyte.subspectrum.proc.instructions.arith16.SBCHLss
import com.subbyte.subspectrum.proc.instructions.arith8.ADCAHL
import com.subbyte.subspectrum.proc.instructions.shift.RLHL
import com.subbyte.subspectrum.proc.instructions.shift.RLIXd
import com.subbyte.subspectrum.proc.instructions.shift.RLIYd
import com.subbyte.subspectrum.proc.instructions.shift.RLr
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
import com.subbyte.subspectrum.proc.instructions.bit.BITbHL
import com.subbyte.subspectrum.proc.instructions.bit.BITbIXd
import com.subbyte.subspectrum.proc.instructions.bit.BITbIYd
import com.subbyte.subspectrum.proc.instructions.bit.BITbr
import com.subbyte.subspectrum.proc.instructions.bit.SETbHL
import com.subbyte.subspectrum.proc.instructions.bit.SETbIXd
import com.subbyte.subspectrum.proc.instructions.bit.SETbIYd
import com.subbyte.subspectrum.proc.instructions.bit.SETbr
import com.subbyte.subspectrum.proc.instructions.bit.RESbHL
import com.subbyte.subspectrum.proc.instructions.bit.RESbIXd
import com.subbyte.subspectrum.proc.instructions.bit.RESbIYd
import com.subbyte.subspectrum.proc.instructions.bit.RESbr
import com.subbyte.subspectrum.proc.instructions.call.CALLccnn
import com.subbyte.subspectrum.proc.instructions.call.CALLnn
import com.subbyte.subspectrum.proc.instructions.call.RET
import com.subbyte.subspectrum.proc.instructions.call.RETcc
import com.subbyte.subspectrum.proc.instructions.call.RETI
import com.subbyte.subspectrum.proc.instructions.call.RETN
import com.subbyte.subspectrum.proc.instructions.call.RSTp
import com.subbyte.subspectrum.proc.instructions.jump.JPnn
import com.subbyte.subspectrum.proc.instructions.jump.JPccnn
import com.subbyte.subspectrum.proc.instructions.jump.JRd
import com.subbyte.subspectrum.proc.instructions.jump.JRCd
import com.subbyte.subspectrum.proc.instructions.jump.JRNCd
import com.subbyte.subspectrum.proc.instructions.jump.JRZd
import com.subbyte.subspectrum.proc.instructions.jump.JRNZd
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
import com.subbyte.subspectrum.proc.instructions.general.RLD
import com.subbyte.subspectrum.proc.instructions.general.RRD
import com.subbyte.subspectrum.proc.instructions.jump.DJNZd
import com.subbyte.subspectrum.proc.instructions.jump.JPHL
import com.subbyte.subspectrum.proc.instructions.jump.JPIX
import com.subbyte.subspectrum.proc.instructions.jump.JPIY
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
import com.subbyte.subspectrum.proc.instructions.shift.RLA
import com.subbyte.subspectrum.proc.instructions.shift.RLCA
import com.subbyte.subspectrum.proc.instructions.shift.RLCHL
import com.subbyte.subspectrum.proc.instructions.shift.RLCIXd
import com.subbyte.subspectrum.proc.instructions.shift.RLCIYd
import com.subbyte.subspectrum.proc.instructions.shift.RLCr
import com.subbyte.subspectrum.proc.instructions.shift.RRA
import com.subbyte.subspectrum.proc.instructions.shift.RRCA
import com.subbyte.subspectrum.proc.instructions.shift.RRCHL
import com.subbyte.subspectrum.proc.instructions.shift.RRCIXd
import com.subbyte.subspectrum.proc.instructions.shift.RRCIYd
import com.subbyte.subspectrum.proc.instructions.shift.RRCr
import com.subbyte.subspectrum.proc.instructions.shift.RRHL
import com.subbyte.subspectrum.proc.instructions.shift.RRIXd
import com.subbyte.subspectrum.proc.instructions.shift.RRIYd
import com.subbyte.subspectrum.proc.instructions.shift.RRr
import com.subbyte.subspectrum.proc.instructions.shift.SLAHL
import com.subbyte.subspectrum.proc.instructions.shift.SLAIXd
import com.subbyte.subspectrum.proc.instructions.shift.SLAIYd
import com.subbyte.subspectrum.proc.instructions.shift.SLAr
import com.subbyte.subspectrum.proc.instructions.shift.SRAHL
import com.subbyte.subspectrum.proc.instructions.shift.SRAIXd
import com.subbyte.subspectrum.proc.instructions.shift.SRAIYd
import com.subbyte.subspectrum.proc.instructions.shift.SRAr
import com.subbyte.subspectrum.proc.instructions.shift.SRLHL
import com.subbyte.subspectrum.proc.instructions.shift.SRLIXd
import com.subbyte.subspectrum.proc.instructions.shift.SRLIYd
import com.subbyte.subspectrum.proc.instructions.shift.SRLr

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
        DECIYd,

        // arith16
        ADDHLss,
        ADCHLss,
        SBCHLss,
        ADDIXss,
        ADDIYss,
        INCss,
        INCIX,
        INCIY,
        DECss,
        DECIX,
        DECIY,

        // shift
        RLCA,
        RLA,
        RRCA,
        RRA,
        RLCr,
        RLCHL,
        RLCIXd,
        RLCIYd,
        RLr,
        RLHL,
        RLIXd,
        RLIYd,
        RRCr,
        RRCHL,
        RRCIXd,
        RRCIYd,
        RRr,
        RRHL,
        RRIXd,
        RRIYd,
        SLAr,
        SLAHL,
        SLAIXd,
        SLAIYd,
        SRAr,
        SRAHL,
        SRAIXd,
        SRAIYd,
        SRLr,
        SRLHL,
        SRLIXd,
        SRLIYd,
        RLD,
        RRD,

        // bit
        BITbr,
        BITbHL,
        BITbIXd,
        BITbIYd,
        SETbr,
        SETbHL,
        SETbIXd,
        SETbIYd,
        RESbr,
        RESbHL,
        RESbIXd,
        RESbIYd,

        // jump
        JPnn,
        JPccnn,
        JRd,
        JRCd,
        JRNCd,
        JRZd,
        JRNZd,
        JPHL,
        JPIX,
        JPIY,
        DJNZd,

        // call
        CALLnn,
        CALLccnn,
        RET,
        RETcc,
        RETI,
        RETN,
        RSTp
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
