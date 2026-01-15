package com.subbyte.subspectrum.proc.instructions.shift

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SRLrTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = SRLr.decode(0xCB38L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xCB.toByte(), instruction.bytes[0])
        assertEquals(0x38.toByte(), instruction.bytes[1])

        val srlr = instruction as SRLr
        assertEquals(RegisterCode.B, srlr.sourceRegister)
    }

    @Test
    fun executeLogicalShiftRightWithCarry() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x03.toByte()) // 00000011

        val instruction = SRLr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x38.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x01.toByte(), Registers.registerSet.getRegister(RegisterCode.B)) // 00000001 (shifted right, MSB set to 0)
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry set to bit 0 (1)
    }

    @Test
    fun executeLogicalShiftRightWithCarryFalse() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x04.toByte()) // 00000100

        val instruction = SRLr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x38.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x02.toByte(), Registers.registerSet.getRegister(RegisterCode.B)) // 00000010 (shifted right, MSB set to 0)
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag()) // Carry set to bit 0 (0)
    }

    @Test
    fun executeLogicalShiftRightNegativeToPositive() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x85.toByte()) // 10000101 (negative)

        val instruction = SRLr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x38.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x42.toByte(), Registers.registerSet.getRegister(RegisterCode.B)) // 01000010 (shifted right, MSB set to 0, becomes positive)
        assertFalse(Registers.registerSet.getSFlag()) // S flag reset
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry set to bit 0 (1)
    }

    @Test
    fun executeLogicalShiftRightToZero() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x00.toByte())

        val instruction = SRLr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x38.toByte()),
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
        val instruction = SRLr(
            address = 0x0000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x38.toByte()),
            sourceRegister = RegisterCode.B
        )

        assertEquals("SRL B", instruction.toString())
    }
}
