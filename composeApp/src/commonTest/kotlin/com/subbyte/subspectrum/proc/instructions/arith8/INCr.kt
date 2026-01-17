package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class INCrTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = INCr.decode(0x04L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x04.toByte(), instruction.bytes[0])

        val incr = instruction as INCr
        assertEquals(RegisterCode.B, incr.sourceRegister)
    }

    @Test
    fun executeIncrementRegister() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x0F.toByte())

        val instruction = INCr(
            address = 0x1000u,
            bytes = byteArrayOf(0x04.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x10.toByte(), Registers.registerSet.getRegister(RegisterCode.B))
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeIncrementToZero() {
        Registers.registerSet.setRegister(RegisterCode.B, 0xFF.toByte())

        val instruction = INCr(
            address = 0x1000u,
            bytes = byteArrayOf(0x04.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getRegister(RegisterCode.B))
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun testSignFlag() {
        Registers.registerSet.setRegister(RegisterCode.C, 0x7F.toByte())

        val instruction = INCr(
            address = 0x1000u,
            bytes = byteArrayOf(0x0C.toByte()),
            sourceRegister = RegisterCode.C
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getSFlag())
        assertEquals(0x80.toByte(), Registers.registerSet.getRegister(RegisterCode.C))
    }

    @Test
    fun testHalfCarryFlag() {
        Registers.registerSet.setRegister(RegisterCode.D, 0x0F.toByte())

        val instruction = INCr(
            address = 0x1000u,
            bytes = byteArrayOf(0x14.toByte()),
            sourceRegister = RegisterCode.D
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getHFlag())
        assertEquals(0x10.toByte(), Registers.registerSet.getRegister(RegisterCode.D))
    }

    @Test
    fun testOverflowFlag() {
        Registers.registerSet.setRegister(RegisterCode.E, 0x7F.toByte())

        val instruction = INCr(
            address = 0x1000u,
            bytes = byteArrayOf(0x1C.toByte()),
            sourceRegister = RegisterCode.E
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getPVFlag())
        assertEquals(0x80.toByte(), Registers.registerSet.getRegister(RegisterCode.E))
    }

    @Test
    fun testNoOverflow() {
        Registers.registerSet.setRegister(RegisterCode.H, 0x40.toByte())

        val instruction = INCr(
            address = 0x1000u,
            bytes = byteArrayOf(0x24.toByte()),
            sourceRegister = RegisterCode.H
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getPVFlag())
        assertEquals(0x41.toByte(), Registers.registerSet.getRegister(RegisterCode.H))
    }

    @Test
    fun testNFlagAlwaysReset() {
        Registers.registerSet.setRegister(RegisterCode.L, 0x10.toByte())
        Registers.registerSet.setNFlag(true)

        val instruction = INCr(
            address = 0x1000u,
            bytes = byteArrayOf(0x2C.toByte()),
            sourceRegister = RegisterCode.L
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getNFlag())
        assertEquals(0x11.toByte(), Registers.registerSet.getRegister(RegisterCode.L))
    }

    @Test
    fun testCFlagNotAffected() {
        Registers.registerSet.setRegister(RegisterCode.A, 0x10.toByte())
        Registers.registerSet.setCFlag(true)

        val instruction = INCr(
            address = 0x1000u,
            bytes = byteArrayOf(0x3C.toByte()),
            sourceRegister = RegisterCode.A
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getCFlag()) // Should remain set
        assertEquals(0x11.toByte(), Registers.registerSet.getRegister(RegisterCode.A))
    }

    @Test
    fun toStringFormat() {
        val instruction = INCr(
            address = 0x0000u,
            bytes = byteArrayOf(0x04.toByte()),
            sourceRegister = RegisterCode.B
        )

        assertEquals("INC B", instruction.toString())
    }
}
