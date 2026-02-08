package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.*

class SBCAnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = SBCAn.decode(0xDEABL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xDE.toByte(), instruction.bytes[0])
        assertEquals(0xAB.toByte(), instruction.bytes[1])

        val sbcAn = instruction as SBCAn
        assertEquals(0xAB.toUByte(), sbcAn.sourceUByte)
    }

    @Test
    fun executeSBCImmediate() {
        Registers.registerSet.setA(0x30.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = SBCAn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xDE.toByte(), 0x10.toByte())),
            sourceUByte = 0x10.toUByte()
        )

        instruction.execute()

        assertEquals(0x20.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun testZeroFlag() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = SBCAn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xDE.toByte(), 0x10.toByte())),
            sourceUByte = 0x10.toUByte()
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun testSignFlag() {
        Registers.registerSet.setA(0x00.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = SBCAn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xDE.toByte(), 0x01.toByte())),
            sourceUByte = 0x01.toUByte()
        )

        instruction.execute()

        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getSFlag())
        assertTrue(Registers.registerSet.getCFlag())
    }

    @Test
    fun testCarryFlag() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = SBCAn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xDE.toByte(), 0x20.toByte())),
            sourceUByte = 0x20.toUByte()
        )

        instruction.execute()

        assertEquals(0xF0.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getCFlag())
    }

    @Test
    fun testCarryFlagWithCarryIn() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setCFlag(true)

        val instruction = SBCAn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xDE.toByte(), 0x10.toByte())),
            sourceUByte = 0x10.toUByte()
        )

        instruction.execute()

        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getSFlag())
    }

    @Test
    fun testHalfCarryFlag() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = SBCAn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xDE.toByte(), 0x01.toByte())),
            sourceUByte = 0x01.toUByte()
        )

        instruction.execute()

        assertEquals(0x0F.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun testHalfCarryFlagWithCarryIn() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setCFlag(true)

        val instruction = SBCAn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xDE.toByte(), 0x00.toByte())),
            sourceUByte = 0x00.toUByte()
        )

        instruction.execute()

        assertEquals(0x0F.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun testOverflowFlag() {
        Registers.registerSet.setA(0x80.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = SBCAn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xDE.toByte(), 0x01.toByte())),
            sourceUByte = 0x01.toUByte()
        )

        instruction.execute()

        assertEquals(0x7F.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getPVFlag())
    }

    @Test
    fun testNoOverflow() {
        Registers.registerSet.setA(0x50.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = SBCAn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xDE.toByte(), 0x20.toByte())),
            sourceUByte = 0x20.toUByte()
        )

        instruction.execute()

        assertEquals(0x30.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getPVFlag())
    }

    @Test
    fun testNFlagAlwaysSet() {
        Registers.registerSet.setA(0x30.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = SBCAn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xDE.toByte(), 0x10.toByte())),
            sourceUByte = 0x10.toUByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = SBCAn(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xDE.toByte(), 0xAB.toByte())),
            sourceUByte = 0xAB.toUByte()
        )

        assertEquals("SBC A, ABh", instruction.toString())
    }
}
