package com.subbyte.subspectrum.proc.instructions.control

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.*

class NEGTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = NEG.decode(0xED44L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0x44.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeNegateZero() {
        Registers.registerSet.setA(0x00.toByte())

        val instruction = NEG(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x44.toByte()))
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getSFlag())
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun executeNegatePositive() {
        Registers.registerSet.setA(0x05.toByte())

        val instruction = NEG(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x44.toByte()))
        )

        instruction.execute()

        assertEquals(0xFB.toByte(), Registers.registerSet.getA()) // -5 = 0xFB
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getHFlag()) // Lower nibble 5, borrow from bit 4
        assertFalse(Registers.registerSet.getPVFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag())
    }

    @Test
    fun executeNegateNegative() {
        Registers.registerSet.setA(0xFB.toByte()) // -5

        val instruction = NEG(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x44.toByte()))
        )

        instruction.execute()

        assertEquals(0x05.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getHFlag()) // Lower nibble B=11, borrow
        assertFalse(Registers.registerSet.getPVFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag())
    }

    @Test
    fun executeNegateWithBorrowFromBit4() {
        Registers.registerSet.setA(0x01.toByte())

        val instruction = NEG(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x44.toByte()))
        )

        instruction.execute()

        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getHFlag()) // Lower nibble 1, borrow
        assertFalse(Registers.registerSet.getPVFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag())
    }

    @Test
    fun executeNegateWithoutBorrowFromBit4() {
        Registers.registerSet.setA(0x10.toByte())

        val instruction = NEG(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x44.toByte()))
        )

        instruction.execute()

        assertEquals(0xF0.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag()) // Lower nibble 0, no borrow
        assertFalse(Registers.registerSet.getPVFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag())
    }

    @Test
    fun executeNegateOverflow() {
        Registers.registerSet.setA(0x80.toByte()) // -128

        val instruction = NEG(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x44.toByte()))
        )

        instruction.execute()

        assertEquals(0x80.toByte(), Registers.registerSet.getA()) // -(-128) = -128 in signed, but overflow
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag()) // Lower nibble 0, no borrow
        assertTrue(Registers.registerSet.getPVFlag()) // Overflow
        assertTrue(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = NEG(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x44.toByte()))
        )

        assertEquals("NEG", instruction.toString())
    }
}
