package com.subbyte.subspectrum.proc.instructions

import BitPattern
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.IndexedPrefixMode
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
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

interface IndexedByteRemappable

data class DecodedInstruction(
    val instruction: Instruction,
    val opcodeFetchCount: Int
)

private data class PrefixWrappedInstruction(
    override val address: Address,
    override val bytes: DataByteArray,
    private val sourceInstruction: Instruction,
    private val extraTStates: Int,
    private val indexedPrefixMode: IndexedPrefixMode?
) : Instruction {
    override fun getTStates(): Int = sourceInstruction.getTStates() + extraTStates

    override fun execute() {
        Registers.withIndexedPrefixMode(indexedPrefixMode) {
            sourceInstruction.execute()
        }
    }

    override fun toString(): String = sourceInstruction.toString()
}

object Instructions {
    private data class IndexPrefixState(
        val mode: IndexedPrefixMode,
        val prefixBytes: ByteArray,
        val opcodeAddress: Address,
    )

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
        SLLIXd,
        SLLIYd,

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
        val indexPrefixState = parseIndexPrefixes(pc)

        // Handle non-prefixed instructions
        if (indexPrefixState == null){
            return decodeByDefinitions(pc)
        }

        // Handle CB/ED prefix exceptions
        val opcode = Memory.memorySet.getMemoryCell(indexPrefixState.opcodeAddress)
        if (opcode == 0xCB.toByte()) {
            val lastPrefixAddress = indexPrefixState.opcodeAddress.dec()
            val decoded = decodeByDefinitions(lastPrefixAddress)
            return wrapWithPrefixes(
                originalAddress = pc,
                decoded = decoded,
                prefixBytes = indexPrefixState.prefixBytes,
                baseAlreadyContainsLastPrefix = true,
                indexedPrefixMode = null,
            )
        }
        if (opcode == 0xED.toByte()) {
            val decoded = decodeByDefinitions(indexPrefixState.opcodeAddress)
            return wrapWithPrefixes(
                originalAddress = pc,
                decoded = decoded,
                prefixBytes = indexPrefixState.prefixBytes,
                baseAlreadyContainsLastPrefix = false,
                indexedPrefixMode = null,
            )
        }

        // Handle instructions with opcodes with prefixes
        val lastPrefixAddress = indexPrefixState.opcodeAddress.dec()
        val prefixedDecoded = tryDecodeByDefinitions(lastPrefixAddress)
        if (prefixedDecoded != null) {
            return wrapWithPrefixes(
                originalAddress = pc,
                decoded = prefixedDecoded,
                prefixBytes = indexPrefixState.prefixBytes,
                baseAlreadyContainsLastPrefix = true,
                indexedPrefixMode = null,
            )
        }

        // Handle all instructions based on prefix
        val decoded = decodeByDefinitions(indexPrefixState.opcodeAddress)
        return wrapWithPrefixes(
            originalAddress = pc,
            decoded = decoded,
            prefixBytes = indexPrefixState.prefixBytes,
            baseAlreadyContainsLastPrefix = false,
            indexedPrefixMode = if (decoded.instruction is IndexedByteRemappable) indexPrefixState.mode else null,
        )
    }

    private fun parseIndexPrefixes(pc: Address): IndexPrefixState? {
        var currentAddress = pc
        var mode: IndexedPrefixMode? = null
        val prefixBytes = mutableListOf<Byte>()

        while (true) {
            when (Memory.memorySet.getMemoryCell(currentAddress)) {
                0xDD.toByte() -> {
                    mode = IndexedPrefixMode.DDIX
                    prefixBytes.add(0xDD.toByte())
                    currentAddress = currentAddress.inc()
                }
                0xFD.toByte() -> {
                    mode = IndexedPrefixMode.FDIY
                    prefixBytes.add(0xFD.toByte())
                    currentAddress = currentAddress.inc()
                }
                else -> break
            }
        }

        if (prefixBytes.isEmpty()) {
            return null
        }

        return IndexPrefixState(
            mode = mode ?: error("Missing index prefix mode"),
            prefixBytes = prefixBytes.toByteArray(),
            opcodeAddress = currentAddress,
        )
    }

    private fun wrapWithPrefixes(
        originalAddress: Address,
        decoded: DecodedInstruction,
        prefixBytes: ByteArray,
        baseAlreadyContainsLastPrefix: Boolean,
        indexedPrefixMode: IndexedPrefixMode?,
    ): DecodedInstruction {
        val extraPrefixByteCount = prefixBytes.size - if (baseAlreadyContainsLastPrefix) 1 else 0
        val sourceBytes = decoded.instruction.bytes
        val wrappedBytes = ByteArray(extraPrefixByteCount + sourceBytes.size).apply {
            prefixBytes.copyInto(this, endIndex = extraPrefixByteCount)
            for (index in 0 until sourceBytes.size) {
                this[extraPrefixByteCount + index] = sourceBytes[index]
            }
        }

        return DecodedInstruction(
            instruction = PrefixWrappedInstruction(
                address = originalAddress,
                bytes = DataByteArray(wrappedBytes),
                sourceInstruction = decoded.instruction,
                extraTStates = extraPrefixByteCount * 4,
                indexedPrefixMode = indexedPrefixMode,
            ),
            opcodeFetchCount = decoded.opcodeFetchCount + extraPrefixByteCount,
        )
    }

    private fun decodeByDefinitions(pc: Address): DecodedInstruction {
        return tryDecodeByDefinitions(pc) ?: run {
            val opcode = Memory.memorySet.getMemoryCell(pc)
            error("Unknown opcode 0x${opcode} at 0x${pc}")
        }
    }
    private fun tryDecodeByDefinitions(pc: Address): DecodedInstruction? {
        for (def in definitions) {
            val pattern = def.bitPattern
            val byteCount = pattern.byteCount

            // Read bytes as long (big-endian)
            var word = 0L
            for (index in 0 until byteCount) {
                val b = Memory.memorySet.getMemoryCell((pc.toInt() + index).toUShort())
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

        return null
    }
}
