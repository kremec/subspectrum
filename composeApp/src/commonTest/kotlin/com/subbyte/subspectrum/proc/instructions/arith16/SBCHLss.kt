package com.subbyte.subspectrum.proc.instructions.arith16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterPairCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class SBCHLssTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = SBCHLss.decode(0xED42L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0x42.toByte(), instruction.bytes[1])

        val sbcHlss = instruction as SBCHLss
        assertEquals(RegisterPairCode.BC, sbcHlss.sourceRegisterPairCode)
    }

    @Test
    fun executeSbcHLBCWithNoCarry() {
        Registers.registerSet.setHL(0x2000.toShort())
        Registers.registerSet.setBC(0x0F00.toShort())
        Registers.registerSet.setCFlag(false)

        val instruction = SBCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x42.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x1100.toShort(), Registers.registerSet.getHL())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeSbcHLDEWithCarry() {
        Registers.registerSet.setHL(0x2000.toShort())
        Registers.registerSet.setDE(0x0F00.toShort())
        Registers.registerSet.setCFlag(true)

        val instruction = SBCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x52.toByte()),
            sourceRegisterPairCode = RegisterPairCode.DE
        )

        instruction.execute()

        assertEquals(0x10FF.toShort(), Registers.registerSet.getHL())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeSbcHLHL() {
        Registers.registerSet.setHL(0x1000.toShort())
        Registers.registerSet.setCFlag(false)

        val instruction = SBCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x62.toByte()),
            sourceRegisterPairCode = RegisterPairCode.HL
        )

        instruction.execute()

        assertEquals(0x0000.toShort(), Registers.registerSet.getHL())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeSbcHLSP() {
        Registers.registerSet.setHL(0x2000.toShort())
        Registers.specialPurposeRegisters.setSP(0x0F00.toShort())
        Registers.registerSet.setCFlag(true)

        val instruction = SBCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x72.toByte()),
            sourceRegisterPairCode = RegisterPairCode.SP
        )

        instruction.execute()

        assertEquals(0x10FF.toShort(), Registers.registerSet.getHL())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun testHFlagSet() {
        // Test H flag set when borrow from bit 12
        Registers.registerSet.setHL(0x1000.toShort()) // 0x1000 & 0xFFF = 0x000
        Registers.registerSet.setBC(0x0001.toShort()) // 0x0001 & 0xFFF = 0x001
        Registers.registerSet.setCFlag(false)         // carry = 0

        val instruction = SBCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x42.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x0FFF.toShort(), Registers.registerSet.getHL())
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
    }

    @Test
    fun testHFlagReset() {
        // Test H flag reset when no borrow from bit 12
        Registers.registerSet.setHL(0x1001.toShort()) // 0x1001 & 0xFFF = 0x001
        Registers.registerSet.setBC(0x0001.toShort()) // 0x0001 & 0xFFF = 0x001
        Registers.registerSet.setCFlag(false)         // carry = 0

        val instruction = SBCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x42.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x1000.toShort(), Registers.registerSet.getHL())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
    }

    @Test
    fun testCFlagSet() {
        // Test C flag set when borrow occurs, H flag also set
        Registers.registerSet.setHL(0x0000.toShort()) // 0x0000 & 0xFFF = 0x000
        Registers.registerSet.setBC(0x0001.toShort()) // 0x0001 & 0xFFF = 0x001
        Registers.registerSet.setCFlag(false)         // carry = 0

        val instruction = SBCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x42.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0xFFFF.toShort(), Registers.registerSet.getHL())
        assertTrue(Registers.registerSet.getHFlag())
        assertTrue(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
    }

    @Test
    fun testCFlagReset() {
        // Test C flag reset when no borrow
        Registers.registerSet.setHL(0x0002.toShort())
        Registers.registerSet.setBC(0x0001.toShort())
        Registers.registerSet.setCFlag(false)

        val instruction = SBCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x42.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x0001.toShort(), Registers.registerSet.getHL())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
    }

    @Test
    fun testPVFlagOverflow() {
        // Test PV flag set for signed overflow
        // 0x8000 - 0x0001 - carry(0) = 0x7FFF (overflow from negative to positive)
        Registers.registerSet.setHL(0x8000.toShort())
        Registers.registerSet.setBC(0x0000.toShort())
        Registers.registerSet.setCFlag(true)

        val instruction = SBCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x42.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x7FFF.toShort(), Registers.registerSet.getHL())
        assertTrue(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
    }

    @Test
    fun testPVFlagNoOverflow() {
        // Test PV flag reset when no overflow
        Registers.registerSet.setHL(0x8001.toShort())
        Registers.registerSet.setBC(0x0000.toShort())
        Registers.registerSet.setCFlag(true)

        val instruction = SBCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x42.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x8000.toShort(), Registers.registerSet.getHL())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
    }

    @Test
    fun testZeroFlag() {
        // Test Z flag set when result is zero
        Registers.registerSet.setHL(0x0001.toShort())
        Registers.registerSet.setBC(0x0000.toShort())
        Registers.registerSet.setCFlag(true)

        val instruction = SBCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x42.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x0000.toShort(), Registers.registerSet.getHL())
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun testSignFlag() {
        // Test S flag set when result is negative
        Registers.registerSet.setHL(0x0000.toShort())
        Registers.registerSet.setBC(0x0001.toShort())
        Registers.registerSet.setCFlag(false)

        val instruction = SBCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x42.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0xFFFF.toShort(), Registers.registerSet.getHL())
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertTrue(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = SBCHLss(
            address = 0x0000u,
            bytes = byteArrayOf(0xED.toByte(), 0x42.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        assertEquals("SBC HL, BC", instruction.toString())
    }
}
