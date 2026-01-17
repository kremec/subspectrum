package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class XORrTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = XORr.decode(0xA9L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0xA9.toByte(), instruction.bytes[0])

        val xorr = instruction as XORr
        assertEquals(RegisterCode.C, xorr.sourceRegister)
    }

    @Test
    fun executeXorRegisters() {
        Registers.registerSet.setA(0x0F.toByte())
        Registers.registerSet.setRegister(RegisterCode.C, 0x0F.toByte())

        val instruction = XORr(
            address = 0x1000u,
            bytes = byteArrayOf(0xA9.toByte()),
            sourceRegister = RegisterCode.C
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
        Registers.registerSet.setRegister(RegisterCode.B, 0x80.toByte())

        val instruction = XORr(
            address = 0x1000u,
            bytes = byteArrayOf(0xA8.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getSFlag())
        assertEquals(0x80.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testParityEven() {
        Registers.registerSet.setA(0x0F.toByte()) // 00001111 (odd parity)
        Registers.registerSet.setRegister(RegisterCode.C, 0x05.toByte()) // 00000101 (odd parity)

        val instruction = XORr(
            address = 0x1000u,
            bytes = byteArrayOf(0xA9.toByte()),
            sourceRegister = RegisterCode.C
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getPVFlag()) // Result 0x0A has even parity
        assertEquals(0x0A.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testParityOdd() {
        Registers.registerSet.setA(0x0F.toByte()) // 00001111 (odd parity)
        Registers.registerSet.setRegister(RegisterCode.D, 0x0E.toByte()) // 00001110 (even parity)

        val instruction = XORr(
            address = 0x1000u,
            bytes = byteArrayOf(0xAA.toByte()),
            sourceRegister = RegisterCode.D
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getPVFlag()) // Result 0x01 has odd parity
        assertEquals(0x01.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testHFlagAlwaysReset() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setRegister(RegisterCode.E, 0x00.toByte())

        val instruction = XORr(
            address = 0x1000u,
            bytes = byteArrayOf(0xAB.toByte()),
            sourceRegister = RegisterCode.E
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getHFlag())
        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testNFlagAlwaysReset() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setRegister(RegisterCode.H, 0xFF.toByte())
        Registers.registerSet.setNFlag(true)

        val instruction = XORr(
            address = 0x1000u,
            bytes = byteArrayOf(0xAC.toByte()),
            sourceRegister = RegisterCode.H
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getNFlag())
        assertEquals(0x00.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testCFlagAlwaysReset() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setRegister(RegisterCode.L, 0xFF.toByte())
        Registers.registerSet.setCFlag(true)

        val instruction = XORr(
            address = 0x1000u,
            bytes = byteArrayOf(0xAD.toByte()),
            sourceRegister = RegisterCode.L
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getCFlag())
        assertEquals(0x00.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun toStringFormat() {
        val instruction = XORr(
            address = 0x0000u,
            bytes = byteArrayOf(0xA9.toByte()),
            sourceRegister = RegisterCode.C
        )

        assertEquals("XOR C", instruction.toString())
    }
}
