package com.subbyte.subspectrum.proc.instructions.shift

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SLArTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = SLAr.decode(0xCB20L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xCB.toByte(), instruction.bytes[0])
        assertEquals(0x20.toByte(), instruction.bytes[1])

        val slar = instruction as SLAr
        assertEquals(RegisterCode.B, slar.sourceRegister)
    }

    @Test
    fun executeArithmeticShiftLeftWithCarry() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x80.toByte()) // 10000000

        val instruction = SLAr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x20.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getRegister(RegisterCode.B)) // 00000000 (shifted left, bit 7 was 1)
        assertFalse(Registers.registerSet.getSFlag())
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry set to bit 7
    }

    @Test
    fun executeArithmeticShiftLeftWithoutCarry() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x40.toByte()) // 01000000

        val instruction = SLAr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x20.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x80.toByte(), Registers.registerSet.getRegister(RegisterCode.B)) // 10000000 (shifted left)
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag()) // Carry not set because bit 7 was 0
    }

    @Test
    fun executeArithmeticShiftLeftToZero() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x00.toByte())

        val instruction = SLAr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x20.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getRegister(RegisterCode.B))
        assertFalse(Registers.registerSet.getSFlag())
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = SLAr(
            address = 0x0000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x20.toByte()),
            sourceRegister = RegisterCode.B
        )

        assertEquals("SLA B", instruction.toString())
    }
}
