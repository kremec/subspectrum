package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ORIYdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ORIYd.decode(0xFDB605L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0xFD.toByte(), instruction.bytes[0])
        assertEquals(0xB6.toByte(), instruction.bytes[1])
        assertEquals(0x05.toByte(), instruction.bytes[2])

        val orIYd = instruction as ORIYd
        assertEquals(0x05.toByte(), orIYd.displacement)
    }

    @Test
    fun executeOrWithIYOffset() {
        Registers.registerSet.setA(0x0F.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0xF0.toByte())

        val instruction = ORIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xB6.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun testZeroFlag() {
        Registers.registerSet.setA(0x00.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x00.toByte())

        val instruction = ORIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xB6.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun testSignFlag() {
        Registers.registerSet.setA(0x00.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x80.toByte())

        val instruction = ORIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xB6.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getSFlag())
        assertEquals(0x80.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testParityEven() {
        Registers.registerSet.setA(0x00.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x0A.toByte())

        val instruction = ORIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xB6.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getPVFlag())
        assertEquals(0x0A.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testParityOdd() {
        Registers.registerSet.setA(0x01.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x00.toByte())

        val instruction = ORIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xB6.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getPVFlag())
        assertEquals(0x01.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testHFlagAlwaysReset() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0xFF.toByte())

        val instruction = ORIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xB6.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getHFlag())
        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testNFlagAlwaysReset() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0xFF.toByte())
        Registers.registerSet.setNFlag(true)

        val instruction = ORIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xB6.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getNFlag())
        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testCFlagAlwaysReset() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0xFF.toByte())
        Registers.registerSet.setCFlag(true)

        val instruction = ORIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xB6.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getCFlag())
        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun toStringFormat() {
        val instruction = ORIYd(
            address = 0x0000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xB6.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        assertEquals("OR (IY + 5)", instruction.toString())
    }
}
