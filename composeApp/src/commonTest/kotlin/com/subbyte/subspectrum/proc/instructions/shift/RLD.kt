package com.subbyte.subspectrum.proc.instructions.shift

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RLDTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = RLD.decode(0xED6FL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0x6F.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeRotateLeftDecimal() {
        Registers.registerSet.setHL(0x2000.toShort())
        Registers.registerSet.setA(0x12.toByte())  // A = 0001 0010
        Memory.memorySet.setMemoryCell(0x2000u, 0x34.toByte())  // Memory = 0011 0100

        val instruction = RLD(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x6F.toByte())
        )

        instruction.execute()

        // Expected result: A becomes 0001 0011 (0x13), Memory becomes 0100 0010 (0x42)
        assertEquals(0x13.toByte(), Registers.registerSet.getA())
        assertEquals(0x42.toByte(), Memory.memorySet.getMemoryCell(0x2000u))

        // Flags: S=false, Z=false, H=false, P/V=?, N=false, C=unchanged
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
        // C flag should be unchanged (was false, should remain false)
    }

    @Test
    fun executeRotateLeftDecimalWithZeroResult() {
        Registers.registerSet.setHL(0x2000.toShort())
        Registers.registerSet.setA(0x00.toByte())  // A = 0000 0000
        Memory.memorySet.setMemoryCell(0x2000u, 0x00.toByte())  // Memory = 0000 0000

        val instruction = RLD(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x6F.toByte())
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getA())
        assertEquals(0x00.toByte(), Memory.memorySet.getMemoryCell(0x2000u))

        // Z flag should be set
        assertTrue(Registers.registerSet.getZFlag())
    }

    @Test
    fun executeRotateLeftDecimalWithNegativeResult() {
        Registers.registerSet.setHL(0x2000.toShort())
        Registers.registerSet.setA(0x80.toByte())  // A = 1000 0000
        Memory.memorySet.setMemoryCell(0x2000u, 0x00.toByte())  // Memory = 0000 0000

        val instruction = RLD(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x6F.toByte())
        )

        instruction.execute()

        // Result: A becomes 1000 0000 (negative), Memory becomes 0000 0000
        assertEquals(0x80.toByte(), Registers.registerSet.getA())
        assertEquals(0x00.toByte(), Memory.memorySet.getMemoryCell(0x2000u))

        // S flag should be set (negative result)
        assertTrue(Registers.registerSet.getSFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = RLD(
            address = 0x0000u,
            bytes = byteArrayOf(0xED.toByte(), 0x6F.toByte())
        )

        assertEquals("RLD", instruction.toString())
    }
}
