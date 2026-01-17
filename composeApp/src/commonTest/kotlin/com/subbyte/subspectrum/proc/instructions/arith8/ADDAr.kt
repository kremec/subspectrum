package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ADDArTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ADDAr.decode(0x81L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x81.toByte(), instruction.bytes[0])

        val addAr = instruction as ADDAr
        assertEquals(RegisterCode.C, addAr.sourceRegister)
    }

    @Test
    fun executeAddRegisters() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setRegister(RegisterCode.C, 0x20.toByte())

        val instruction = ADDAr(
            address = 0x1000u,
            bytes = byteArrayOf(0x81.toByte()),
            sourceRegister = RegisterCode.C
        )

        instruction.execute()

        assertEquals(0x30.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun testZeroFlag() {
        Registers.registerSet.setA(0x00.toByte())
        Registers.registerSet.setRegister(RegisterCode.B, 0x00.toByte())

        val instruction = ADDAr(
            address = 0x1000u,
            bytes = byteArrayOf(0x80.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getSFlag())
    }

    @Test
    fun testSignFlag() {
        Registers.registerSet.setA(0x7F.toByte())
        Registers.registerSet.setRegister(RegisterCode.B, 0x01.toByte())

        val instruction = ADDAr(
            address = 0x1000u,
            bytes = byteArrayOf(0x80.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x80.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
    }

    @Test
    fun testCarryFlag() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setRegister(RegisterCode.B, 0x01.toByte())

        val instruction = ADDAr(
            address = 0x1000u,
            bytes = byteArrayOf(0x80.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getZFlag())
    }

    @Test
    fun testHalfCarryFlag() {
        Registers.registerSet.setA(0x0F.toByte())
        Registers.registerSet.setRegister(RegisterCode.B, 0x01.toByte())

        val instruction = ADDAr(
            address = 0x1000u,
            bytes = byteArrayOf(0x80.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x10.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun testOverflowFlag() {
        Registers.registerSet.setA(0x7F.toByte())
        Registers.registerSet.setRegister(RegisterCode.B, 0x01.toByte())

        val instruction = ADDAr(
            address = 0x1000u,
            bytes = byteArrayOf(0x80.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x80.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getPVFlag())
    }

    @Test
    fun testNoOverflow() {
        Registers.registerSet.setA(0x40.toByte())
        Registers.registerSet.setRegister(RegisterCode.B, 0x20.toByte())

        val instruction = ADDAr(
            address = 0x1000u,
            bytes = byteArrayOf(0x80.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x60.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getPVFlag())
    }

    @Test
    fun testNFlagAlwaysReset() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setRegister(RegisterCode.B, 0x20.toByte())
        Registers.registerSet.setNFlag(true)

        val instruction = ADDAr(
            address = 0x1000u,
            bytes = byteArrayOf(0x80.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = ADDAr(
            address = 0x0000u,
            bytes = byteArrayOf(0x81.toByte()),
            sourceRegister = RegisterCode.C
        )

        assertEquals("ADD A, C", instruction.toString())
    }
}
