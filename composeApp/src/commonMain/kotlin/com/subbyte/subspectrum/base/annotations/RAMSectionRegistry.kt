package com.subbyte.subspectrum.base.annotations

import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.MemorySet
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.uWordFromBytes

object RAMSectionRegistry {
    fun allRegions(memorySet: MemorySet): List<MemoryRegionAnnotation> {
        return fixedRegions + dynamicRegions(memorySet)
    }

    val fixedRegions: List<MemoryRegionAnnotation> = listOf(
        MemoryRegionAnnotation(0x0000u, 0x3FFFu, "ROM"),
        MemoryRegionAnnotation(0x4000u, 0x57FFu, "Screen file"),
        MemoryRegionAnnotation(0x5800u, 0x5AFFu, "Attribute file"),
        MemoryRegionAnnotation(0x5B00u, 0x5BFFu, "Printer buffer"),
        MemoryRegionAnnotation(0x5C00u, 0x5CB5u, "System variables"),
    )

    fun dynamicRegions(memorySet: MemorySet): List<MemoryRegionAnnotation> {
        fun systemPointer(address: Address): Address {
            val lowByte = memorySet.getMemoryCell(address)
            val highByte = memorySet.getMemoryCell(address.inc())
            return Pair(highByte, lowByte).uWordFromBytes()
        }

        val chans = systemPointer(0x5C4Fu)
        val prog = systemPointer(0x5C53u)
        val vars = systemPointer(0x5C4Bu)
        val eLine = systemPointer(0x5C59u)
        val worksp = systemPointer(0x5C61u)
        val stkbot = systemPointer(0x5C63u)
        val stkend = systemPointer(0x5C65u)
        val ramtop = systemPointer(0x5CB2u)
        val pRamt = systemPointer(0x5CB4u)
        val udg = systemPointer(0x5C7Bu)
        val sp = Registers.specialPurposeRegisters.getSP().toUShort()

        return buildList {
            if (0x5CB6u < chans) {
                add(MemoryRegionAnnotation(0x5CB6u, (chans.toInt() - 1).toUShort(), "Microdrive maps"))
            }

            if (chans < prog) {
                add(MemoryRegionAnnotation(chans, (prog.toInt() - 2).toUShort(), "Channel information"))
                val terminator = (prog.toInt() - 1).toUShort()
                add(MemoryRegionAnnotation(terminator, terminator, "80h channel terminator"))
            }

            if (prog < vars) {
                add(MemoryRegionAnnotation(prog, (vars.toInt() - 1).toUShort(), "BASIC program"))
            }

            if (vars < eLine) {
                add(MemoryRegionAnnotation(vars, (eLine.toInt() - 2).toUShort(), "Variables"))
                val terminator = (eLine.toInt() - 1).toUShort()
                add(MemoryRegionAnnotation(terminator, terminator, "80h variables terminator"))
            }

            if (eLine < worksp) {
                val editLine = "Command/program line being edited"
                val editLineEnd = (worksp.toInt() - 1).toUShort()
                if (worksp.toInt() - eLine.toInt() >= 2) {
                    add(MemoryRegionAnnotation(eLine, (worksp.toInt() - 3).toUShort(), editLine))
                    val newLineTerminator = (worksp.toInt() - 2).toUShort()
                    add(MemoryRegionAnnotation(newLineTerminator, newLineTerminator, "NL edit-line terminator"))
                    add(MemoryRegionAnnotation(editLineEnd, editLineEnd, "80h edit-line terminator"))
                } else {
                    add(MemoryRegionAnnotation(eLine, editLineEnd, editLine))
                }
            }

            if (worksp < stkbot) {
                add(MemoryRegionAnnotation(worksp, (stkbot.toInt() - 1).toUShort(), "Temporary workspace"))
            }
            if (stkbot < stkend) {
                add(MemoryRegionAnnotation(stkbot, (stkend.toInt() - 1).toUShort(), "Calculator stack"))
            }
            if (sp > stkend && sp <= ramtop) {
                add(MemoryRegionAnnotation(stkend, (sp.toInt() - 1).toUShort(), "Spare space"))
                add(MemoryRegionAnnotation(sp, ramtop, "Machine stack"))
            } else {
                add(MemoryRegionAnnotation(stkend, ramtop, "Spare space"))
            }
            add(MemoryRegionAnnotation(udg, pRamt, "User-defined graphics"))
        }.sortedBy { it.start.toInt() }
    }
}
