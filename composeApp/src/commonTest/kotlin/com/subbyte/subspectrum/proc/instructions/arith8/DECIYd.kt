package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DECIYdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = DECIYd.decode(0xFD3505L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0xFD.toByte(), instruction.bytes[0])
        assertEquals(0x35.toByte(), instruction.bytes[1])
        assertEquals(0x05.toByte(), instruction.bytes[2])

        val decIYd = instruction as DECIYd
        assertEquals(0x05.toByte(), decIYd.displacement)
    }

    @Test
    fun executeDecrementWithIYOffset() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x10.toByte())

        val instruction = DECIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x35.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertEquals(0x0F.toByte(), Memory.memorySet.getMemoryCell(0x2005u))
        assertFalse(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun testSignFlag() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x81.toByte())

        val instruction = DECIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x35.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getSFlag())
        assertEquals(0x80.toByte(), Memory.memorySet.getMemoryCell(0x2005u))
    }

    @Test
    fun testHalfCarryFlag() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x10.toByte())

        val instruction = DECIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x35.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getHFlag())
        assertEquals(0x0F.toByte(), Memory.memorySet.getMemoryCell(0x2005u))
    }

    @Test
    fun testOverflowFlag() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x80.toByte())

        val instruction = DECIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x35.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getPVFlag())
        assertEquals(0x7F.toByte(), Memory.memorySet.getMemoryCell(0x2005u))
    }

    @Test
    fun testNoOverflow() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x41.toByte())

        val instruction = DECIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x35.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getPVFlag())
        assertEquals(0x40.toByte(), Memory.memorySet.getMemoryCell(0x2005u))
    }

    @Test
    fun testNFlagAlwaysSet() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x11.toByte())
        Registers.registerSet.setNFlag(false)

        val instruction = DECIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x35.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getNFlag())
        assertEquals(0x10.toByte(), Memory.memorySet.getMemoryCell(0x2005u))
    }

    @Test
    fun testCFlagNotAffected() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x11.toByte())
        Registers.registerSet.setCFlag(true)

        val instruction = DECIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x35.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getCFlag()) // Should remain set
        assertEquals(0x10.toByte(), Memory.memorySet.getMemoryCell(0x2005u))
    }

    @Test
    fun toStringFormat() {
        val instruction = DECIYd(
            address = 0x0000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x35.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        assertEquals("DEC (IY + 5)", instruction.toString())
    }
}
