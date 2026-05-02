package com.subbyte.subspectrum.proc.instructions

import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.RegisterPairSSCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.arith16.ADDHLss
import com.subbyte.subspectrum.proc.instructions.arith8.ADDAr
import com.subbyte.subspectrum.proc.instructions.arith8.CPr
import com.subbyte.subspectrum.proc.instructions.bit.BITbHL
import com.subbyte.subspectrum.proc.instructions.bit.BITbIXd
import com.subbyte.subspectrum.proc.instructions.bit.BITbr
import com.subbyte.subspectrum.proc.instructions.block.CPI
import com.subbyte.subspectrum.proc.instructions.block.LDI
import com.subbyte.subspectrum.proc.instructions.control.SCF
import com.subbyte.subspectrum.proc.instructions.io.INI
import com.subbyte.subspectrum.proc.instructions.jump.JRd
import com.subbyte.subspectrum.proc.instructions.load8.LDrIXd
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UndocumentedFlagSemanticsTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        IO.ioPortSet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun registerSetExposesUndocumentedFlagBits() {
        Registers.registerSet.setXFFlag(true)
        Registers.registerSet.setYFFlag(true)
        assertTrue(Registers.registerSet.getXFFlag())
        assertTrue(Registers.registerSet.getYFFlag())

        Registers.registerSet.setXFFlag(false)
        Registers.registerSet.setYFFlag(true)
        assertFalse(Registers.registerSet.getXFFlag())
        assertTrue(Registers.registerSet.getYFFlag())

        Registers.registerSet.setF(0x28)
        assertTrue(Registers.registerSet.getXFFlag())
        assertTrue(Registers.registerSet.getYFFlag())
    }

    @Test
    fun addCopiesXYFlagsFromResult() {
        Registers.registerSet.setA(0x10)
        Registers.registerSet.setB(0x18)

        ADDAr(0x1000u, DataByteArray(byteArrayOf(0x80.toByte())), RegisterCode.B).execute()

        assertEquals(0x28.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getXFFlag())
        assertTrue(Registers.registerSet.getYFFlag())
    }

    @Test
    fun compareCopiesXYFlagsFromOperand() {
        Registers.registerSet.setA(0x28)
        Registers.registerSet.setB(0x20)

        CPr(0x1000u, DataByteArray(byteArrayOf(0xB8.toByte())), RegisterCode.B).execute()

        assertFalse(Registers.registerSet.getXFFlag())
        assertTrue(Registers.registerSet.getYFFlag())
    }

    @Test
    fun scfCopiesXYFlagsFromAccumulator() {
        Registers.registerSet.setA(0x28)

        SCF(0x1000u, DataByteArray(byteArrayOf(0x37))).execute()

        assertTrue(Registers.registerSet.getXFFlag())
        assertTrue(Registers.registerSet.getYFFlag())
    }

    @Test
    fun bitRegisterUsesTestedBitForXYFlagsAndPvMatchesZ() {
        Registers.registerSet.setB(0x20)
        Registers.registerSet.setCFlag(true)

        BITbr(0x1000u, DataByteArray(byteArrayOf(0xCB.toByte(), 0x68)), 5, RegisterCode.B).execute()

        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertTrue(Registers.registerSet.getYFFlag())
        assertFalse(Registers.registerSet.getXFFlag())
        assertTrue(Registers.registerSet.getCFlag())
    }

    @Test
    fun bitIndexedUsesEffectiveAddressHighByteForXYFlags() {
        Registers.specialPurposeRegisters.setIX(0x6800.toShort())
        Memory.memorySet.setMemoryCell(0x6820u, 0x00)

        BITbIXd(0x1000u, DataByteArray(byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x20, 0x46)), 0, 0x20).execute()

        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getPVFlag())
        assertTrue(Registers.registerSet.getYFFlag())
        assertTrue(Registers.registerSet.getXFFlag())
    }

    @Test
    fun bitHlUsesMemptrHighByteForXYFlags() {
        Registers.registerSet.setHL(0x8000.toShort())
        Memory.memorySet.setMemoryCell(0x8000u, 0x00)
        Registers.specialPurposeRegisters.setMEMPTR(0x2800.toShort())

        BITbHL(0x1000u, DataByteArray(byteArrayOf(0xCB.toByte(), 0x46)), 0).execute()

        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getYFFlag())
        assertTrue(Registers.registerSet.getXFFlag())
    }

    @Test
    fun memptrSourcesFollowDocumentedChapterFourSources() {
        Registers.registerSet.setHL(0x2800.toShort())
        Registers.registerSet.setBC(0x0100.toShort())
        ADDHLss(0x1000u, DataByteArray(byteArrayOf(0x09)), RegisterPairSSCode.BC).execute()
        assertEquals(0x2800.toShort(), Registers.specialPurposeRegisters.getMEMPTR())

        Registers.specialPurposeRegisters.setIX(0x6800.toShort())
        Memory.memorySet.setMemoryCell(0x6820u, 0x42)
        LDrIXd(0x1000u, DataByteArray(byteArrayOf(0xDD.toByte(), 0x46, 0x20)), RegisterCode.B, 0x20).execute()
        assertEquals(0x6820.toShort(), Registers.specialPurposeRegisters.getMEMPTR())

        Registers.specialPurposeRegisters.setPC(0x8100.toShort())
        JRd(0x1000u, DataByteArray(byteArrayOf(0x18, 0x10)), 0x10).execute()
        assertEquals(0x8110.toShort(), Registers.specialPurposeRegisters.getMEMPTR())
    }

    @Test
    fun ldiUsesBlockTransferXYFormula() {
        Registers.registerSet.setA(0x01)
        Registers.registerSet.setHL(0x8000.toShort())
        Registers.registerSet.setDE(0x9000.toShort())
        Registers.registerSet.setBC(0x0002.toShort())
        Memory.memorySet.setMemoryCell(0x8000u, 0x08)

        LDI(0x1000u, DataByteArray(byteArrayOf(0xED.toByte(), 0xA0.toByte()))).execute()

        assertFalse(Registers.registerSet.getYFFlag())
        assertTrue(Registers.registerSet.getXFFlag())
        assertTrue(Registers.registerSet.getPVFlag())
    }

    @Test
    fun cpiUsesCompareBlockXYFormulaAndPreservesCarry() {
        Registers.registerSet.setA(0x10)
        Registers.registerSet.setHL(0x8000.toShort())
        Registers.registerSet.setBC(0x0002.toShort())
        Registers.registerSet.setCFlag(true)
        Memory.memorySet.setMemoryCell(0x8000u, 0x07)

        CPI(0x1000u, DataByteArray(byteArrayOf(0xED.toByte(), 0xA1.toByte()))).execute()

        assertFalse(Registers.registerSet.getYFFlag())
        assertTrue(Registers.registerSet.getXFFlag())
        assertTrue(Registers.registerSet.getCFlag())
    }

    @Test
    fun iniUsesBlockIoFlagFormula() {
        Registers.registerSet.setB(0x21)
        Registers.registerSet.setC(0x01)
        Registers.registerSet.setHL(0x8000.toShort())
        IO.ioPortSet.setIO(0x2101u, 0x81.toByte())

        INI(0x1000u, DataByteArray(byteArrayOf(0xED.toByte(), 0xA2.toByte()))).execute()

        assertEquals(0x20.toByte(), Registers.registerSet.getB())
        assertFalse(Registers.registerSet.getXFFlag())
        assertTrue(Registers.registerSet.getYFFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
    }
}
