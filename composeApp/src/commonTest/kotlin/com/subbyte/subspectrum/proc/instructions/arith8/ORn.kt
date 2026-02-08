package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.*

class ORnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ORn.decode(0xF6ABL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xF6.toByte(), instruction.bytes[0])
        assertEquals(0xAB.toByte(), instruction.bytes[1])

        val orn = instruction as ORn
        assertEquals(0xAB.toUByte(), orn.sourceUByte)
    }

    @Test
    fun executeOrImmediate() {
        Registers.registerSet.setA(0x0F.toByte())

        val instruction = ORn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xF6.toByte(), 0xF0.toByte())),
            sourceUByte = 0xF0.toUByte()
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

        val instruction = ORn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xF6.toByte(), 0x00.toByte())),
            sourceUByte = 0x00.toUByte()
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

        val instruction = ORn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xF6.toByte(), 0x80.toByte())),
            sourceUByte = 0x80.toUByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getSFlag())
        assertEquals(0x80.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testParityEven() {
        Registers.registerSet.setA(0x00.toByte())

        val instruction = ORn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xF6.toByte(), 0x0A.toByte())),
            sourceUByte = 0x0A.toUByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getPVFlag())
        assertEquals(0x0A.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testParityOdd() {
        Registers.registerSet.setA(0x01.toByte())

        val instruction = ORn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xF6.toByte(), 0x00.toByte())),
            sourceUByte = 0x00.toUByte()
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getPVFlag())
        assertEquals(0x01.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testHFlagAlwaysReset() {
        Registers.registerSet.setA(0xFF.toByte())

        val instruction = ORn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xF6.toByte(), 0xFF.toByte())),
            sourceUByte = 0xFF.toUByte()
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getHFlag())
        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testNFlagAlwaysReset() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setNFlag(true)

        val instruction = ORn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xF6.toByte(), 0xFF.toByte())),
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

        val instruction = ORn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xF6.toByte(), 0xFF.toByte())),
            sourceUByte = 0xFF.toUByte()
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getCFlag())
        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun toStringFormat() {
        val instruction = ORn(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xF6.toByte(), 0xAB.toByte())),
            sourceUByte = 0xAB.toUByte()
        )

        assertEquals("OR ABh", instruction.toString())
    }
}
