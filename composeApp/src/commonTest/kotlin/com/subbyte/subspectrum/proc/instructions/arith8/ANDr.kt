package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ANDrTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ANDr.decode(0xA1L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0xA1.toByte(), instruction.bytes[0])

        val andr = instruction as ANDr
        assertEquals(RegisterCode.C, andr.sourceRegister)
    }

    @Test
    fun executeAndRegisters() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setRegister(RegisterCode.C, 0x0F.toByte())

        val instruction = ANDr(
            address = 0x1000u,
            bytes = byteArrayOf(0xA1.toByte()),
            sourceRegister = RegisterCode.C
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
        Registers.registerSet.setRegister(RegisterCode.B, 0x0F.toByte())

        val instruction = ANDr(
            address = 0x1000u,
            bytes = byteArrayOf(0xA0.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getHFlag()) // H always set for AND
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun testSignFlag() {
        Registers.registerSet.setA(0x80.toByte())
        Registers.registerSet.setRegister(RegisterCode.C, 0xFF.toByte())

        val instruction = ANDr(
            address = 0x1000u,
            bytes = byteArrayOf(0xA1.toByte()),
            sourceRegister = RegisterCode.C
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getSFlag())
        assertEquals(0x80.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testParityEven() {
        Registers.registerSet.setA(0x0F.toByte()) // 00001111 (odd parity)
        Registers.registerSet.setRegister(RegisterCode.D, 0x0A.toByte()) // 00001010 (even parity)

        val instruction = ANDr(
            address = 0x1000u,
            bytes = byteArrayOf(0xA2.toByte()),
            sourceRegister = RegisterCode.D
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getPVFlag()) // Result 0x0A has even parity
        assertEquals(0x0A.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testParityOdd() {
        Registers.registerSet.setA(0x0F.toByte()) // 00001111 (odd parity)
        Registers.registerSet.setRegister(RegisterCode.E, 0x01.toByte()) // 00000001 (odd parity)

        val instruction = ANDr(
            address = 0x1000u,
            bytes = byteArrayOf(0xA3.toByte()),
            sourceRegister = RegisterCode.E
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getPVFlag()) // Result 0x01 has odd parity
        assertEquals(0x01.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testHFlagAlwaysSet() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setRegister(RegisterCode.H, 0x00.toByte())

        val instruction = ANDr(
            address = 0x1000u,
            bytes = byteArrayOf(0xA4.toByte()),
            sourceRegister = RegisterCode.H
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getHFlag())
        assertEquals(0x00.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun testNFlagAlwaysReset() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setRegister(RegisterCode.L, 0xFF.toByte())
        Registers.registerSet.setNFlag(true)

        val instruction = ANDr(
            address = 0x1000u,
            bytes = byteArrayOf(0xA5.toByte()),
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

        val instruction = ANDr(
            address = 0x1000u,
            bytes = byteArrayOf(0xA7.toByte()),
            sourceRegister = RegisterCode.A
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getCFlag())
        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun toStringFormat() {
        val instruction = ANDr(
            address = 0x0000u,
            bytes = byteArrayOf(0xA1.toByte()),
            sourceRegister = RegisterCode.C
        )

        assertEquals("AND C", instruction.toString())
    }
}
