package com.subbyte.subspectrum.proc.instructions.shift

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SRLIYdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = SRLIYd.decode(0xFDCB003EL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(4, instruction.bytes.size)
        assertEquals(0xFD.toByte(), instruction.bytes[0])
        assertEquals(0xCB.toByte(), instruction.bytes[1])
        assertEquals(0x00.toByte(), instruction.bytes[2])
        assertEquals(0x3E.toByte(), instruction.bytes[3])

        val srlIYd = instruction as SRLIYd
        assertEquals(0x00.toByte(), srlIYd.displacement)
    }

    @Test
    fun executeLogicalShiftRightMemoryIYdWithCarry() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x03.toByte()) // 00000011

        val instruction = SRLIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x3E.toByte()),
            displacement = 0x00.toByte()
        )

        instruction.execute()

        assertEquals(0x01.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // 00000001 (shifted right, MSB set to 0)
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry set to bit 0 (1)
    }

    @Test
    fun executeLogicalShiftRightMemoryIYdWithCarryFalse() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x04.toByte()) // 00000100

        val instruction = SRLIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x3E.toByte()),
            displacement = 0x00.toByte()
        )

        instruction.execute()

        assertEquals(0x02.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // 00000010 (shifted right, MSB set to 0)
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag()) // Carry set to bit 0 (0)
    }

    @Test
    fun executeLogicalShiftRightMemoryIYdNegativeToPositive() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x85.toByte()) // 10000101 (negative)

        val instruction = SRLIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x3E.toByte()),
            displacement = 0x00.toByte()
        )

        instruction.execute()

        assertEquals(0x42.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // 01000010 (shifted right, MSB set to 0, becomes positive)
        assertFalse(Registers.registerSet.getSFlag()) // S flag reset
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry set to bit 0 (1)
    }

    @Test
    fun executeLogicalShiftRightToZero() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x00.toByte())

        val instruction = SRLIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x3E.toByte()),
            displacement = 0x00.toByte()
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
        val instruction = SRLIYd(
            address = 0x0000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x3E.toByte()),
            displacement = 0x00.toByte()
        )

        assertEquals("SRL (IY + 0)", instruction.toString())
    }
}
