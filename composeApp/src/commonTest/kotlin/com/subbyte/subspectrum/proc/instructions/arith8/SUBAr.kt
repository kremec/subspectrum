package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class SUBArTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = SUBAr.decode(0x91L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x91.toByte(), instruction.bytes[0])

        val subAr = instruction as SUBAr
        assertEquals(RegisterCode.C, subAr.sourceRegister)
    }

    @Test
    fun executeSubRegisters() {
        Registers.registerSet.setA(0x30.toByte())
        Registers.registerSet.setRegister(RegisterCode.C, 0x10.toByte())

        val instruction = SUBAr(
            address = 0x1000u,
            bytes = byteArrayOf(0x91.toByte()),
            sourceRegister = RegisterCode.C
        )

        instruction.execute()

        assertEquals(0x20.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun testZeroFlag() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setRegister(RegisterCode.B, 0x10.toByte())

        val instruction = SUBAr(
            address = 0x1000u,
            bytes = byteArrayOf(0x90.toByte()),
            sourceRegister = RegisterCode.B
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
        Registers.registerSet.setRegister(RegisterCode.B, 0x01.toByte())

        val instruction = SUBAr(
            address = 0x1000u,
            bytes = byteArrayOf(0x90.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getSFlag())
        assertTrue(Registers.registerSet.getCFlag())
    }

    @Test
    fun testCarryFlag() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setRegister(RegisterCode.B, 0x20.toByte())

        val instruction = SUBAr(
            address = 0x1000u,
            bytes = byteArrayOf(0x90.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0xF0.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getCFlag())
    }

    @Test
    fun testHalfCarryFlag() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setRegister(RegisterCode.B, 0x01.toByte())

        val instruction = SUBAr(
            address = 0x1000u,
            bytes = byteArrayOf(0x90.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x0F.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun testOverflowFlag() {
        Registers.registerSet.setA(0x80.toByte())
        Registers.registerSet.setRegister(RegisterCode.B, 0x01.toByte())

        val instruction = SUBAr(
            address = 0x1000u,
            bytes = byteArrayOf(0x90.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x7F.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getPVFlag())
    }

    @Test
    fun testNoOverflow() {
        Registers.registerSet.setA(0x50.toByte())
        Registers.registerSet.setRegister(RegisterCode.B, 0x20.toByte())

        val instruction = SUBAr(
            address = 0x1000u,
            bytes = byteArrayOf(0x90.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x30.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getPVFlag())
    }

    @Test
    fun testNFlagAlwaysSet() {
        Registers.registerSet.setA(0x30.toByte())
        Registers.registerSet.setRegister(RegisterCode.B, 0x10.toByte())

        val instruction = SUBAr(
            address = 0x1000u,
            bytes = byteArrayOf(0x90.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = SUBAr(
            address = 0x0000u,
            bytes = byteArrayOf(0x91.toByte()),
            sourceRegister = RegisterCode.C
        )

        assertEquals("SUB A, C", instruction.toString())
    }
}
