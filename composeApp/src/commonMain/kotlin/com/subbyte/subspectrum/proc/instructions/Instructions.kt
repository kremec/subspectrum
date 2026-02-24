package com.subbyte.subspectrum.proc.instructions

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.units.DataByteArray

import com.subbyte.subspectrum.proc.instructions.arith16.*
import com.subbyte.subspectrum.proc.instructions.arith8.*
import com.subbyte.subspectrum.proc.instructions.bit.*
import com.subbyte.subspectrum.proc.instructions.block.*
import com.subbyte.subspectrum.proc.instructions.call.*
import com.subbyte.subspectrum.proc.instructions.control.*
import com.subbyte.subspectrum.proc.instructions.ex.*
import com.subbyte.subspectrum.proc.instructions.io.*
import com.subbyte.subspectrum.proc.instructions.jump.*
import com.subbyte.subspectrum.proc.instructions.load16.*
import com.subbyte.subspectrum.proc.instructions.load8.*
import com.subbyte.subspectrum.proc.instructions.shift.*
import com.subbyte.subspectrum.proc.instructions.undocumented.cb.*
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.IM0_1
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.IM0_2
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.IM0_3
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.IM1_1
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.IM2_1
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.INFC
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.LDHLnn_1
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.LDnnHL_1
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.NEG_1
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.NEG_2
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.NEG_3
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.NEG_4
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.NEG_5
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.NEG_6
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.NEG_7
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.NOP_1
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.NOP_2
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.OUTC0
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.RETN_1
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.RETN_2
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.RETN_3
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.RETN_4
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.RETN_5
import com.subbyte.subspectrum.proc.instructions.undocumented.ed.RETN_6

interface Instruction {
    val address: Address
    val bytes: DataByteArray

    fun getTStates(): Int
    fun execute()
}

interface InstructionDefinition {
    val bitPattern: BitPattern
    fun decode(word: Long, address: Address): Instruction
}

data class DecodedInstruction(
    val instruction: Instruction,
    val opcodeFetchCount: Int
)

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
        ADDIXpp,
        ADDIYrr,
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
        RSTp,

        // control
        DAA,
        CPL,
        NEG,
        CCF,
        SCF,
        NOP,
        HALT,
        DI,
        EI,
        IM0,
        IM1,
        IM2,

        // io
        INAn,
        INrC,
        INI,
        INIR,
        IND,
        INDR,
        OUTnA,
        OUTCr,
        OUTI,
        OTIR,
        OUTD,
        OTDR,

        // undocumented 0xCB
        SLLr,
        SLLHL,

        // undocumented 0xED
        NEG_1,
        IM0_1,
        NEG_2,
        RETN_1,
        NEG_3,
        RETN_2,
        LDnnHL_1,
        NEG_4,
        RETN_3,
        IM0_2,
        LDHLnn_1,
        NEG_5,
        RETN_4,
        IM0_3,
        INFC,
        OUTC0,
        NEG_6,
        RETN_5,
        IM1_1,
        NOP_1,
        NEG_7,
        RETN_6,
        IM2_1,
        NOP_2
    )

    fun decode(pc: Address): DecodedInstruction {
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
                try {
                    val instruction = def.decode(word, pc)
                    return DecodedInstruction(instruction, pattern.getOpcodeFetchCount())
                } catch (_: NoSuchElementException) {
                    // Decoding failed (e.g., invalid register code), try next definition
                    continue
                }
            }
        }

        val opcode = Memory.memorySet.getMemoryCell(pc)
        error("Unknown opcode 0x${opcode.toInt() and 0xFF} at 0x${pc.toInt()}")
    }
}
