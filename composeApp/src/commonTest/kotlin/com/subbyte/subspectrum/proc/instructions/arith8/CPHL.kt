package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class CPHLTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = CPHL.decode(0xBEL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0xBE.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeCompareWithMemoryHL() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x10.toByte())

        val instruction = CPHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xBE.toByte())
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertEquals(0x10.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testSignFlag() {
        Registers.registerSet.setA(0x00.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x01.toByte())

        val instruction = CPHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xBE.toByte())
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getSFlag())
        assertTrue(Registers.registerSet.getCFlag())
        assertEquals(0x00.toByte(), Registers.registerSet.getA()) // A unchanged
    }

    @Test
    fun testCarryFlag() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x20.toByte())

        val instruction = CPHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xBE.toByte())
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getCFlag())
        assertEquals(0x10.toByte(), Registers.registerSet.getA()) // A unchanged
    }

    @Test
    fun testHalfCarryFlag() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x01.toByte())

        val instruction = CPHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xBE.toByte())
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertEquals(0x10.toByte(), Registers.registerSet.getA()) // A unchanged
    }

    @Test
    fun testOverflowFlag() {
        Registers.registerSet.setA(0x80.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x01.toByte())

        val instruction = CPHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xBE.toByte())
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getPVFlag())
        assertEquals(0x80.toByte(), Registers.registerSet.getA()) // A unchanged
    }

    @Test
    fun testNoOverflow() {
        Registers.registerSet.setA(0x50.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x20.toByte())

        val instruction = CPHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xBE.toByte())
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getPVFlag())
        assertEquals(0x50.toByte(), Registers.registerSet.getA()) // A unchanged
    }

    @Test
    fun testNFlagAlwaysSet() {
        Registers.registerSet.setA(0x30.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x10.toByte())

        val instruction = CPHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xBE.toByte())
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getNFlag())
        assertEquals(0x30.toByte(), Registers.registerSet.getA()) // A unchanged
    }

    @Test
    fun testARegisterUnchanged() {
        val originalA = 0x42.toByte()
        Registers.registerSet.setA(originalA)
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x10.toByte())

        val instruction = CPHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xBE.toByte())
        )

        instruction.execute()

        assertEquals(originalA, Registers.registerSet.getA())
    }

    @Test
    fun toStringFormat() {
        val instruction = CPHL(
            address = 0x0000u,
            bytes = byteArrayOf(0xBE.toByte())
        )

        assertEquals("CP (HL)", instruction.toString())
    }
}
