package com.subbyte.subspectrum.proc.instructions.shift

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SLAHLTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = SLAHL.decode(0xCB26L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xCB.toByte(), instruction.bytes[0])
        assertEquals(0x26.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeArithmeticShiftLeftMemoryHLWithCarry() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x80.toByte()) // 10000000

        val instruction = SLAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x26.toByte())
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // 00000000 (shifted left, bit 7 was 1)
        assertFalse(Registers.registerSet.getSFlag())
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertTrue(Registers.registerSet.getPVFlag()) // Result 0x00 has even parity
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry set to bit 7
    }

    @Test
    fun executeArithmeticShiftLeftMemoryHLWithoutCarry() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x40.toByte()) // 01000000

        val instruction = SLAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x26.toByte())
        )

        instruction.execute()

        assertEquals(0x80.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // 10000000 (shifted left)
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag()) // Carry not set because bit 7 was 0
    }

    @Test
    fun executeArithmeticShiftLeftToZero() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x00.toByte())

        val instruction = SLAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x26.toByte())
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Memory.memorySet.getMemoryCell(0x2000u))
        assertFalse(Registers.registerSet.getSFlag())
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertTrue(Registers.registerSet.getPVFlag()) // Result 0x00 has even parity
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun testParityEven() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x0C.toByte()) // 00001100

        val instruction = SLAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x26.toByte())
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getPVFlag()) // Result 0x18 has even parity
        assertEquals(0x18.toByte(), Memory.memorySet.getMemoryCell(0x2000u))
    }

    @Test
    fun testParityOdd() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x08.toByte()) // 00001000

        val instruction = SLAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x26.toByte())
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getPVFlag()) // Result 0x10 has odd parity
        assertEquals(0x10.toByte(), Memory.memorySet.getMemoryCell(0x2000u))
    }

    @Test
    fun toStringFormat() {
        val instruction = SLAHL(
            address = 0x0000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x26.toByte())
        )

        assertEquals("SLA (HL)", instruction.toString())
    }
}
