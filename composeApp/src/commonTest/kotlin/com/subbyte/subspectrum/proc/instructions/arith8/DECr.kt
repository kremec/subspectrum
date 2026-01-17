package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DECrTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = DECr.decode(0x05L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x05.toByte(), instruction.bytes[0])

        val decr = instruction as DECr
        assertEquals(RegisterCode.B, decr.sourceRegister)
    }

    @Test
    fun executeDecrementRegister() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x10.toByte())

        val instruction = DECr(
            address = 0x1000u,
            bytes = byteArrayOf(0x05.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x0F.toByte(), Registers.registerSet.getRegister(RegisterCode.B))
        assertFalse(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeDecrementToZero() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x01.toByte())

        val instruction = DECr(
            address = 0x1000u,
            bytes = byteArrayOf(0x05.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getRegister(RegisterCode.B))
        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun testSignFlag() {
        Registers.registerSet.setRegister(RegisterCode.C, 0x81.toByte())

        val instruction = DECr(
            address = 0x1000u,
            bytes = byteArrayOf(0x0D.toByte()),
            sourceRegister = RegisterCode.C
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getSFlag())
        assertEquals(0x80.toByte(), Registers.registerSet.getRegister(RegisterCode.C))
    }

    @Test
    fun testHalfCarryFlag() {
        Registers.registerSet.setRegister(RegisterCode.D, 0x10.toByte())

        val instruction = DECr(
            address = 0x1000u,
            bytes = byteArrayOf(0x15.toByte()),
            sourceRegister = RegisterCode.D
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getHFlag())
        assertEquals(0x0F.toByte(), Registers.registerSet.getRegister(RegisterCode.D))
    }

    @Test
    fun testOverflowFlag() {
        Registers.registerSet.setRegister(RegisterCode.E, 0x80.toByte())

        val instruction = DECr(
            address = 0x1000u,
            bytes = byteArrayOf(0x1D.toByte()),
            sourceRegister = RegisterCode.E
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getPVFlag())
        assertEquals(0x7F.toByte(), Registers.registerSet.getRegister(RegisterCode.E))
    }

    @Test
    fun testNoOverflow() {
        Registers.registerSet.setRegister(RegisterCode.H, 0x41.toByte())

        val instruction = DECr(
            address = 0x1000u,
            bytes = byteArrayOf(0x25.toByte()),
            sourceRegister = RegisterCode.H
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getPVFlag())
        assertEquals(0x40.toByte(), Registers.registerSet.getRegister(RegisterCode.H))
    }

    @Test
    fun testNFlagAlwaysSet() {
        Registers.registerSet.setRegister(RegisterCode.L, 0x11.toByte())
        Registers.registerSet.setNFlag(false)

        val instruction = DECr(
            address = 0x1000u,
            bytes = byteArrayOf(0x2D.toByte()),
            sourceRegister = RegisterCode.L
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getNFlag())
        assertEquals(0x10.toByte(), Registers.registerSet.getRegister(RegisterCode.L))
    }

    @Test
    fun testCFlagNotAffected() {
        Registers.registerSet.setRegister(RegisterCode.A, 0x11.toByte())
        Registers.registerSet.setCFlag(true)

        val instruction = DECr(
            address = 0x1000u,
            bytes = byteArrayOf(0x3D.toByte()),
            sourceRegister = RegisterCode.A
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getCFlag()) // Should remain set
        assertEquals(0x10.toByte(), Registers.registerSet.getRegister(RegisterCode.A))
    }

    @Test
    fun toStringFormat() {
        val instruction = DECr(
            address = 0x0000u,
            bytes = byteArrayOf(0x05.toByte()),
            sourceRegister = RegisterCode.B
        )

        assertEquals("DEC B", instruction.toString())
    }
}
