package com.subbyte.subspectrum.proc.instructions.shift

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SRArTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = SRAr.decode(0xCB28L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xCB.toByte(), instruction.bytes[0])
        assertEquals(0x28.toByte(), instruction.bytes[1])

        val srar = instruction as SRAr
        assertEquals(RegisterCode.B, srar.sourceRegister)
    }

    @Test
    fun executeArithmeticShiftRightPositiveWithCarry() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x03.toByte()) // 00000011

        val instruction = SRAr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x28.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x01.toByte(), Registers.registerSet.getRegister(RegisterCode.B)) // 00000001 (shifted right, sign bit preserved as 0)
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry set to bit 0 (1)
    }

    @Test
    fun executeArithmeticShiftRightNegativeWithCarry() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x85.toByte()) // 10000101

        val instruction = SRAr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x28.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0xC2.toByte(), Registers.registerSet.getRegister(RegisterCode.B)) // 11000010 (shifted right, sign bit preserved as 1)
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry set to bit 0 (1)
    }

    @Test
    fun executeArithmeticShiftRightNegativeWithoutCarry() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x84.toByte()) // 10000100

        val instruction = SRAr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x28.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0xC2.toByte(), Registers.registerSet.getRegister(RegisterCode.B)) // 11000010 (shifted right, sign bit preserved as 1)
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag()) // Carry set to bit 0 (0)
    }

    @Test
    fun executeArithmeticShiftRightToZero() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x00.toByte())

        val instruction = SRAr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x28.toByte()),
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
        val instruction = SRAr(
            address = 0x0000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x28.toByte()),
            sourceRegister = RegisterCode.B
        )

        assertEquals("SRA B", instruction.toString())
    }
}
