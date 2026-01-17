package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class XORnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = XORn.decode(0xEEABL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xEE.toByte(), instruction.bytes[0])
        assertEquals(0xAB.toByte(), instruction.bytes[1])

        val xorn = instruction as XORn
        assertEquals(0xAB.toByte(), xorn.sourceByte)
    }

    @Test
    fun executeXorImmediate() {
        Registers.registerSet.setA(0x0F.toByte())

        val instruction = XORn(
            address = 0x1000u,
            bytes = byteArrayOf(0xEE.toByte(), 0x0F.toByte()),
            sourceByte = 0x0F.toByte()
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

        val instruction = XORn(
            address = 0x1000u,
            bytes = byteArrayOf(0xEE.toByte(), 0x80.toByte()),
            sourceByte = 0x80.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getSFlag())
        assertEquals(0x80.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testParityEven() {
        Registers.registerSet.setA(0x0F.toByte())

        val instruction = XORn(
            address = 0x1000u,
            bytes = byteArrayOf(0xEE.toByte(), 0x05.toByte()),
            sourceByte = 0x05.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getPVFlag())
        assertEquals(0x0A.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testParityOdd() {
        Registers.registerSet.setA(0x0F.toByte())

        val instruction = XORn(
            address = 0x1000u,
            bytes = byteArrayOf(0xEE.toByte(), 0x0E.toByte()),
            sourceByte = 0x0E.toByte()
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getPVFlag())
        assertEquals(0x01.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testHFlagAlwaysReset() {
        Registers.registerSet.setA(0xFF.toByte())

        val instruction = XORn(
            address = 0x1000u,
            bytes = byteArrayOf(0xEE.toByte(), 0x00.toByte()),
            sourceByte = 0x00.toByte()
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getHFlag())
        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testNFlagAlwaysReset() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setNFlag(true)

        val instruction = XORn(
            address = 0x1000u,
            bytes = byteArrayOf(0xEE.toByte(), 0xFF.toByte()),
            sourceByte = 0xFF.toByte()
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getNFlag())
        assertEquals(0x00.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testCFlagAlwaysReset() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setCFlag(true)

        val instruction = XORn(
            address = 0x1000u,
            bytes = byteArrayOf(0xEE.toByte(), 0xFF.toByte()),
            sourceByte = 0xFF.toByte()
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getCFlag())
        assertEquals(0x00.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun toStringFormat() {
        val instruction = XORn(
            address = 0x0000u,
            bytes = byteArrayOf(0xEE.toByte(), 0xAB.toByte()),
            sourceByte = 0xAB.toByte()
        )

        assertEquals("XOR -85", instruction.toString())
    }
}
