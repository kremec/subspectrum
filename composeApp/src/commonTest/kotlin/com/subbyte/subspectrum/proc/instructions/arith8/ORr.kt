package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ORrTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ORr.decode(0xB1L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0xB1.toByte(), instruction.bytes[0])

        val orr = instruction as ORr
        assertEquals(RegisterCode.C, orr.sourceRegister)
    }

    @Test
    fun executeOrRegisters() {
        Registers.registerSet.setA(0x0F.toByte())
        Registers.registerSet.setRegister(RegisterCode.C, 0xF0.toByte())

        val instruction = ORr(
            address = 0x1000u,
            bytes = byteArrayOf(0xB1.toByte()),
            sourceRegister = RegisterCode.C
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
        Registers.registerSet.setRegister(RegisterCode.B, 0x00.toByte())

        val instruction = ORr(
            address = 0x1000u,
            bytes = byteArrayOf(0xB0.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag()) // H always false for OR
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun testSignFlag() {
        Registers.registerSet.setA(0x00.toByte())
        Registers.registerSet.setRegister(RegisterCode.C, 0x80.toByte())

        val instruction = ORr(
            address = 0x1000u,
            bytes = byteArrayOf(0xB1.toByte()),
            sourceRegister = RegisterCode.C
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getSFlag())
        assertEquals(0x80.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testParityEven() {
        Registers.registerSet.setA(0x00.toByte()) // 00000000 (even parity)
        Registers.registerSet.setRegister(RegisterCode.D, 0x0A.toByte()) // 00001010 (even parity)

        val instruction = ORr(
            address = 0x1000u,
            bytes = byteArrayOf(0xB2.toByte()),
            sourceRegister = RegisterCode.D
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getPVFlag()) // Result 0x0A has even parity
        assertEquals(0x0A.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testParityOdd() {
        Registers.registerSet.setA(0x01.toByte()) // 00000001 (odd parity)
        Registers.registerSet.setRegister(RegisterCode.E, 0x00.toByte()) // 00000000 (even parity)

        val instruction = ORr(
            address = 0x1000u,
            bytes = byteArrayOf(0xB3.toByte()),
            sourceRegister = RegisterCode.E
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getPVFlag()) // Result 0x01 has odd parity
        assertEquals(0x01.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testHFlagAlwaysReset() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setRegister(RegisterCode.H, 0xFF.toByte())

        val instruction = ORr(
            address = 0x1000u,
            bytes = byteArrayOf(0xB4.toByte()),
            sourceRegister = RegisterCode.H
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getHFlag())
        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testNFlagAlwaysReset() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setRegister(RegisterCode.L, 0xFF.toByte())
        Registers.registerSet.setNFlag(true)

        val instruction = ORr(
            address = 0x1000u,
            bytes = byteArrayOf(0xB5.toByte()),
            sourceRegister = RegisterCode.L
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getNFlag())
        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testCFlagAlwaysReset() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setRegister(RegisterCode.A, 0xFF.toByte())
        Registers.registerSet.setCFlag(true)

        val instruction = ORr(
            address = 0x1000u,
            bytes = byteArrayOf(0xB7.toByte()),
            sourceRegister = RegisterCode.A
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getCFlag())
        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun toStringFormat() {
        val instruction = ORr(
            address = 0x0000u,
            bytes = byteArrayOf(0xB1.toByte()),
            sourceRegister = RegisterCode.C
        )

        assertEquals("OR C", instruction.toString())
    }
}
