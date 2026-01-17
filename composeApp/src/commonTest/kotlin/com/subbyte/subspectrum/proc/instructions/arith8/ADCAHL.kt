package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ADCAHLTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ADCAHL.decode(0x8EL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x8E.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeADCWithMemoryHL() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x20.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = ADCAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0x8E.toByte())
        )

        instruction.execute()

        assertEquals(0x30.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeADCWithCarrySet() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x20.toByte())
        Registers.registerSet.setCFlag(true)

        val instruction = ADCAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0x8E.toByte())
        )

        instruction.execute()

        assertEquals(0x31.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testZeroFlag() {
        Registers.registerSet.setA(0x00.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x00.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = ADCAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0x8E.toByte())
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getSFlag())
    }

    @Test
    fun testSignFlag() {
        Registers.registerSet.setA(0x7F.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x01.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = ADCAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0x8E.toByte())
        )

        instruction.execute()

        assertEquals(0x80.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
    }

    @Test
    fun testCarryFlag() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x01.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = ADCAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0x8E.toByte())
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getZFlag())
    }

    @Test
    fun testCarryFlagWithCarryIn() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0xFF.toByte())
        Registers.registerSet.setCFlag(true)

        val instruction = ADCAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0x8E.toByte())
        )

        instruction.execute()

        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getCFlag())
    }

    @Test
    fun testHalfCarryFlag() {
        Registers.registerSet.setA(0x0F.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x01.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = ADCAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0x8E.toByte())
        )

        instruction.execute()

        assertEquals(0x10.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun testHalfCarryFlagWithCarryIn() {
        Registers.registerSet.setA(0x0F.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x00.toByte())
        Registers.registerSet.setCFlag(true)

        val instruction = ADCAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0x8E.toByte())
        )

        instruction.execute()

        assertEquals(0x10.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getHFlag())
    }

    @Test
    fun testOverflowFlagPositiveResult() {
        Registers.registerSet.setA(0x7F.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x01.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = ADCAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0x8E.toByte())
        )

        instruction.execute()

        assertEquals(0x80.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getPVFlag())
    }

    @Test
    fun testOverflowFlagNegativeResult() {
        Registers.registerSet.setA(0x80.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0xFF.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = ADCAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0x8E.toByte())
        )

        instruction.execute()

        assertEquals(0x7F.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getPVFlag())
        assertTrue(Registers.registerSet.getCFlag())
    }

    @Test
    fun testNoOverflow() {
        Registers.registerSet.setA(0x40.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x20.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = ADCAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0x8E.toByte())
        )

        instruction.execute()

        assertEquals(0x60.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getPVFlag())
    }

    @Test
    fun testNFlagAlwaysReset() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x20.toByte())
        Registers.registerSet.setCFlag(false)
        Registers.registerSet.setNFlag(true)

        val instruction = ADCAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0x8E.toByte())
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = ADCAHL(
            address = 0x0000u,
            bytes = byteArrayOf(0x8E.toByte())
        )

        assertEquals("ADC A, (HL)", instruction.toString())
    }
}
