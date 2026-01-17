package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ADDAIYdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ADDAIYd.decode(0xFD8605L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0xFD.toByte(), instruction.bytes[0])
        assertEquals(0x86.toByte(), instruction.bytes[1])
        assertEquals(0x05.toByte(), instruction.bytes[2])

        val addAIYd = instruction as ADDAIYd
        assertEquals(0x05.toByte(), addAIYd.displacement)
    }

    @Test
    fun executeAddWithIYOffset() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x20.toByte())

        val instruction = ADDAIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x86.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertEquals(0x30.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun testZeroFlag() {
        Registers.registerSet.setA(0x00.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x00.toByte())

        val instruction = ADDAIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x86.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getSFlag())
    }

    @Test
    fun testSignFlag() {
        Registers.registerSet.setA(0x7F.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x01.toByte())

        val instruction = ADDAIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x86.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertEquals(0x80.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getSFlag())
    }

    @Test
    fun testCarryFlag() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x01.toByte())

        val instruction = ADDAIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x86.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getZFlag())
    }

    @Test
    fun testHalfCarryFlag() {
        Registers.registerSet.setA(0x0F.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x01.toByte())

        val instruction = ADDAIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x86.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertEquals(0x10.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun testOverflowFlag() {
        Registers.registerSet.setA(0x7F.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x01.toByte())

        val instruction = ADDAIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x86.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertEquals(0x80.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getPVFlag())
    }

    @Test
    fun testNFlagAlwaysReset() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x20.toByte())
        Registers.registerSet.setNFlag(true)

        val instruction = ADDAIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x86.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = ADDAIYd(
            address = 0x0000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x86.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        assertEquals("ADD A, (IY + 5)", instruction.toString())
    }
}
