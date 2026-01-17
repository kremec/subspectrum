package com.subbyte.subspectrum.proc.instructions.arith16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterPairCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ADCHLssTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ADCHLss.decode(0xED4AL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0x4A.toByte(), instruction.bytes[1])

        val adcHlss = instruction as ADCHLss
        assertEquals(RegisterPairCode.BC, adcHlss.sourceRegisterPairCode)
    }

    @Test
    fun executeAdcHLBCWithNoCarry() {
        Registers.registerSet.setHL(0x1000.toShort())
        Registers.registerSet.setBC(0x0F00.toShort())
        Registers.registerSet.setCFlag(false)

        val instruction = ADCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x4A.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x1F00.toShort(), Registers.registerSet.getHL())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeAdcHLDEWithCarry() {
        Registers.registerSet.setHL(0x1000.toShort())
        Registers.registerSet.setDE(0x0F00.toShort())
        Registers.registerSet.setCFlag(true)

        val instruction = ADCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x5A.toByte()),
            sourceRegisterPairCode = RegisterPairCode.DE
        )

        instruction.execute()

        assertEquals(0x1F01.toShort(), Registers.registerSet.getHL())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeAdcHLHL() {
        Registers.registerSet.setHL(0x1000.toShort())
        Registers.registerSet.setCFlag(false)

        val instruction = ADCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x6A.toByte()),
            sourceRegisterPairCode = RegisterPairCode.HL
        )

        instruction.execute()

        assertEquals(0x2000.toShort(), Registers.registerSet.getHL())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeAdcHLSP() {
        Registers.registerSet.setHL(0x1000.toShort())
        Registers.specialPurposeRegisters.setSP(0x0F00.toShort())
        Registers.registerSet.setCFlag(true)

        val instruction = ADCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x7A.toByte()),
            sourceRegisterPairCode = RegisterPairCode.SP
        )

        instruction.execute()

        assertEquals(0x1F01.toShort(), Registers.registerSet.getHL())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun testHFlagSet() {
        // Test H flag set when carry from bit 11 with carry input
        Registers.registerSet.setHL(0x0FFF.toShort()) // 0x0FFF & 0xFFF = 0xFFF
        Registers.registerSet.setBC(0x0000.toShort()) // 0x0000 & 0xFFF = 0x000
        Registers.registerSet.setCFlag(true)          // carry = 1

        val instruction = ADCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x4A.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x1000.toShort(), Registers.registerSet.getHL())
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
    }

    @Test
    fun testHFlagReset() {
        // Test H flag reset when no carry from bit 11
        Registers.registerSet.setHL(0x0FFE.toShort()) // 0x0FFE & 0xFFF = 0xFFE
        Registers.registerSet.setBC(0x0000.toShort()) // 0x0000 & 0xFFF = 0x000
        Registers.registerSet.setCFlag(true)          // carry = 1

        val instruction = ADCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x4A.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x0FFF.toShort(), Registers.registerSet.getHL())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
    }

    @Test
    fun testCFlagSet() {
        // Test C flag set when carry from bit 15, H flag also set
        Registers.registerSet.setHL(0xFFFF.toShort()) // 0xFFFF & 0xFFF = 0xFFF
        Registers.registerSet.setBC(0x0000.toShort()) // 0x0000 & 0xFFF = 0x000
        Registers.registerSet.setCFlag(true)          // carry = 1

        val instruction = ADCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x4A.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x0000.toShort(), Registers.registerSet.getHL())
        assertTrue(Registers.registerSet.getHFlag())
        assertTrue(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getSFlag())
        assertTrue(Registers.registerSet.getZFlag())
    }

    @Test
    fun testCFlagReset() {
        // Test C flag reset when no carry from bit 15
        Registers.registerSet.setHL(0xFFFE.toShort())
        Registers.registerSet.setBC(0x0000.toShort())
        Registers.registerSet.setCFlag(true)

        val instruction = ADCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x4A.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0xFFFF.toShort(), Registers.registerSet.getHL())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
    }

    @Test
    fun testPVFlagOverflow() {
        // Test PV flag set for signed overflow
        // 0x7FFF + 0x0001 + carry(1) = 0x8001 (overflow from positive to negative)
        Registers.registerSet.setHL(0x7FFF.toShort())
        Registers.registerSet.setBC(0x0000.toShort())
        Registers.registerSet.setCFlag(true)

        val instruction = ADCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x4A.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x8000.toShort(), Registers.registerSet.getHL())
        assertTrue(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun testPVFlagNoOverflow() {
        // Test PV flag reset when no overflow
        Registers.registerSet.setHL(0x7FFE.toShort())
        Registers.registerSet.setBC(0x0000.toShort())
        Registers.registerSet.setCFlag(true)

        val instruction = ADCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x4A.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x7FFF.toShort(), Registers.registerSet.getHL())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun testZeroFlag() {
        // Test Z flag set when result is zero
        Registers.registerSet.setHL(0x0000.toShort())
        Registers.registerSet.setBC(0x0000.toShort())
        Registers.registerSet.setCFlag(false)

        val instruction = ADCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x4A.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x0000.toShort(), Registers.registerSet.getHL())
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = ADCHLss(
            address = 0x0000u,
            bytes = byteArrayOf(0xED.toByte(), 0x4A.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        assertEquals("ADC HL, BC", instruction.toString())
    }
}
