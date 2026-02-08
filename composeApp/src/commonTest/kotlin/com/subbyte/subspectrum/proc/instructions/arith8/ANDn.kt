package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.*

class ANDnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ANDn.decode(0xE6ABL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xE6.toByte(), instruction.bytes[0])
        assertEquals(0xAB.toByte(), instruction.bytes[1])

        val andn = instruction as ANDn
        assertEquals(0xAB.toUByte(), andn.sourceUByte)
    }

    @Test
    fun executeAndImmediate() {
        Registers.registerSet.setA(0xFF.toByte())

        val instruction = ANDn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xE6.toByte(), 0x0F.toByte())),
            sourceUByte = 0x0F.toUByte()
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

        val instruction = ANDn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xE6.toByte(), 0x0F.toByte())),
            sourceUByte = 0x0F.toUByte()
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

        val instruction = ANDn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xE6.toByte(), 0xFF.toByte())),
            sourceUByte = 0xFF.toUByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getSFlag())
        assertEquals(0x80.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testParityEven() {
        Registers.registerSet.setA(0x0F.toByte())

        val instruction = ANDn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xE6.toByte(), 0x0A.toByte())),
            sourceUByte = 0x0A.toUByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getPVFlag())
        assertEquals(0x0A.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testParityOdd() {
        Registers.registerSet.setA(0x0F.toByte())

        val instruction = ANDn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xE6.toByte(), 0x01.toByte())),
            sourceUByte = 0x01.toUByte()
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getPVFlag())
        assertEquals(0x01.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testHFlagAlwaysSet() {
        Registers.registerSet.setA(0xFF.toByte())

        val instruction = ANDn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xE6.toByte(), 0x00.toByte())),
            sourceUByte = 0x00.toUByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getHFlag())
        assertEquals(0x00.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testNFlagAlwaysReset() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setNFlag(true)

        val instruction = ANDn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xE6.toByte(), 0xFF.toByte())),
            sourceUByte = 0xFF.toUByte()
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getNFlag())
        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testCFlagAlwaysReset() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setCFlag(true)

        val instruction = ANDn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xE6.toByte(), 0xFF.toByte())),
            sourceUByte = 0xFF.toUByte()
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getCFlag())
        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun toStringFormat() {
        val instruction = ANDn(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xE6.toByte(), 0xAB.toByte())),
            sourceUByte = 0xAB.toUByte()
        )

        assertEquals("AND ABh", instruction.toString())
    }
}
