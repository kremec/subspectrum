package com.subbyte.subspectrum.proc.instructions.shift

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SRLIXdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = SRLIXd.decode(0xDDCB003EL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(4, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0xCB.toByte(), instruction.bytes[1])
        assertEquals(0x00.toByte(), instruction.bytes[2])
        assertEquals(0x3E.toByte(), instruction.bytes[3])

        val srlIXd = instruction as SRLIXd
        assertEquals(0x00.toByte(), srlIXd.displacement)
    }

    @Test
    fun executeLogicalShiftRightMemoryIXdWithCarry() {
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x03.toByte()) // 00000011

        val instruction = SRLIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x3E.toByte()),
            displacement = 0x00.toByte()
        )

        instruction.execute()

        assertEquals(0x01.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // 00000001 (shifted right, MSB set to 0)
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag()) // Result 0x01 has odd parity
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry set to bit 0 (1)
    }

    @Test
    fun executeLogicalShiftRightMemoryIXdWithCarryFalse() {
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x04.toByte()) // 00000100

        val instruction = SRLIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x3E.toByte()),
            displacement = 0x00.toByte()
        )

        instruction.execute()

        assertEquals(0x02.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // 00000010 (shifted right, MSB set to 0)
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag()) // Result 0x01 has odd parity
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag()) // Carry set to bit 0 (0)
    }

    @Test
    fun executeLogicalShiftRightMemoryIXdNegativeToPositive() {
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x85.toByte()) // 10000101 (negative)

        val instruction = SRLIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x3E.toByte()),
            displacement = 0x00.toByte()
        )

        instruction.execute()

        assertEquals(0x42.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // 01000010 (shifted right, MSB set to 0, becomes positive)
        assertFalse(Registers.registerSet.getSFlag()) // S flag reset
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertTrue(Registers.registerSet.getPVFlag()) // Result 0x42 has even parity
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry set to bit 0 (1)
    }

    @Test
    fun executeLogicalShiftRightToZero() {
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x00.toByte())

        val instruction = SRLIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x3E.toByte()),
            displacement = 0x00.toByte()
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
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x06.toByte()) // 00000110 (even parity)

        val instruction = SRLIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x3E.toByte()),
            displacement = 0x00.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getPVFlag()) // Result 0x03 has even parity
        assertEquals(0x03.toByte(), Memory.memorySet.getMemoryCell(0x2000u))
    }

    @Test
    fun testParityOdd() {
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x02.toByte()) // 00000010 (even parity)

        val instruction = SRLIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x3E.toByte()),
            displacement = 0x00.toByte()
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getPVFlag()) // Result 0x01 has odd parity
        assertEquals(0x01.toByte(), Memory.memorySet.getMemoryCell(0x2000u))
    }

    @Test
    fun toStringFormat() {
        val instruction = SRLIXd(
            address = 0x0000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x3E.toByte()),
            displacement = 0x00.toByte()
        )

        assertEquals("SRL (IX + 0)", instruction.toString())
    }
}
