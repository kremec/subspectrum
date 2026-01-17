package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class CPIYdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = CPIYd.decode(0xFDBE05L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0xFD.toByte(), instruction.bytes[0])
        assertEquals(0xBE.toByte(), instruction.bytes[1])
        assertEquals(0x05.toByte(), instruction.bytes[2])

        val cpIYd = instruction as CPIYd
        assertEquals(0x05.toByte(), cpIYd.displacement)
    }

    @Test
    fun executeCompareWithIYOffset() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x10.toByte())

        val instruction = CPIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xBE.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertEquals(0x10.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testSignFlag() {
        Registers.registerSet.setA(0x00.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x01.toByte())

        val instruction = CPIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xBE.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getSFlag())
        assertTrue(Registers.registerSet.getCFlag())
        assertEquals(0x00.toByte(), Registers.registerSet.getA()) // A unchanged
    }

    @Test
    fun testCarryFlag() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x20.toByte())

        val instruction = CPIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xBE.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getCFlag())
        assertEquals(0x10.toByte(), Registers.registerSet.getA()) // A unchanged
    }

    @Test
    fun testHalfCarryFlag() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x01.toByte())

        val instruction = CPIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xBE.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertEquals(0x10.toByte(), Registers.registerSet.getA()) // A unchanged
    }

    @Test
    fun testOverflowFlag() {
        Registers.registerSet.setA(0x80.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x01.toByte())

        val instruction = CPIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xBE.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getPVFlag())
        assertEquals(0x80.toByte(), Registers.registerSet.getA()) // A unchanged
    }

    @Test
    fun testNoOverflow() {
        Registers.registerSet.setA(0x50.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x20.toByte())

        val instruction = CPIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xBE.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getPVFlag())
        assertEquals(0x50.toByte(), Registers.registerSet.getA()) // A unchanged
    }

    @Test
    fun testNFlagAlwaysSet() {
        Registers.registerSet.setA(0x30.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x10.toByte())

        val instruction = CPIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xBE.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getNFlag())
        assertEquals(0x30.toByte(), Registers.registerSet.getA()) // A unchanged
    }

    @Test
    fun testARegisterUnchanged() {
        val originalA = 0x42.toByte()
        Registers.registerSet.setA(originalA)
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x10.toByte())

        val instruction = CPIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xBE.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertEquals(originalA, Registers.registerSet.getA())
    }

    @Test
    fun toStringFormat() {
        val instruction = CPIYd(
            address = 0x0000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xBE.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        assertEquals("CP (IY + 5)", instruction.toString())
    }
}
