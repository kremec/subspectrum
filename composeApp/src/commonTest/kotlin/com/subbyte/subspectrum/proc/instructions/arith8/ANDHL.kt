package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ANDHLTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ANDHL.decode(0xA6L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0xA6.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeAndMemoryHL() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x0F.toByte())

        val instruction = ANDHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xA6.toByte())
        )

        instruction.execute()

        assertEquals(0x0F.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun testZeroFlag() {
        Registers.registerSet.setA(0xF0.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x0F.toByte())

        val instruction = ANDHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xA6.toByte())
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun testSignFlag() {
        Registers.registerSet.setA(0x80.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0xFF.toByte())

        val instruction = ANDHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xA6.toByte())
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getSFlag())
        assertEquals(0x80.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testParityEven() {
        Registers.registerSet.setA(0x0F.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x0A.toByte())

        val instruction = ANDHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xA6.toByte())
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getPVFlag())
        assertEquals(0x0A.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testParityOdd() {
        Registers.registerSet.setA(0x0F.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x01.toByte())

        val instruction = ANDHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xA6.toByte())
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getPVFlag())
        assertEquals(0x01.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testHFlagAlwaysSet() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x00.toByte())

        val instruction = ANDHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xA6.toByte())
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getHFlag())
        assertEquals(0x00.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testNFlagAlwaysReset() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0xFF.toByte())
        Registers.registerSet.setNFlag(true)

        val instruction = ANDHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xA6.toByte())
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getNFlag())
        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testCFlagAlwaysReset() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0xFF.toByte())
        Registers.registerSet.setCFlag(true)

        val instruction = ANDHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xA6.toByte())
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getCFlag())
        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun toStringFormat() {
        val instruction = ANDHL(
            address = 0x0000u,
            bytes = byteArrayOf(0xA6.toByte())
        )

        assertEquals("AND (HL)", instruction.toString())
    }
}
