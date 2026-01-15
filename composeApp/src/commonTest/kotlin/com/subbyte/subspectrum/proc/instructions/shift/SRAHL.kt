package com.subbyte.subspectrum.proc.instructions.shift

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SRAHLTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = SRAHL.decode(0xCB2EL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xCB.toByte(), instruction.bytes[0])
        assertEquals(0x2E.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeArithmeticShiftRightMemoryHLPositiveWithCarry() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x03.toByte()) // 00000011

        val instruction = SRAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x2E.toByte())
        )

        instruction.execute()

        assertEquals(0x01.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // 00000001 (shifted right, sign bit preserved as 0)
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry set to bit 0 (1)
    }

    @Test
    fun executeArithmeticShiftRightMemoryHLNegativeWithCarry() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x85.toByte()) // 10000101

        val instruction = SRAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x2E.toByte())
        )

        instruction.execute()

        assertEquals(0xC2.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // 11000010 (shifted right, sign bit preserved as 1)
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry set to bit 0 (1)
    }

    @Test
    fun executeArithmeticShiftRightMemoryHLNegativeWithoutCarry() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x84.toByte()) // 10000100

        val instruction = SRAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x2E.toByte())
        )

        instruction.execute()

        assertEquals(0xC2.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // 11000010 (shifted right, sign bit preserved as 1)
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag()) // Carry set to bit 0 (0)
    }

    @Test
    fun executeArithmeticShiftRightToZero() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x00.toByte())

        val instruction = SRAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x2E.toByte())
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Memory.memorySet.getMemoryCell(0x2000u))
        assertFalse(Registers.registerSet.getSFlag())
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = SRAHL(
            address = 0x0000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x2E.toByte())
        )

        assertEquals("SRA (HL)", instruction.toString())
    }
}
