package com.subbyte.subspectrum.proc.instructions.shift

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SRAIXdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = SRAIXd.decode(0xDDCB002EL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(4, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0xCB.toByte(), instruction.bytes[1])
        assertEquals(0x00.toByte(), instruction.bytes[2])
        assertEquals(0x2E.toByte(), instruction.bytes[3])

        val sraIXd = instruction as SRAIXd
        assertEquals(0x00.toByte(), sraIXd.displacement)
    }

    @Test
    fun executeArithmeticShiftRightMemoryIXdPositiveWithCarry() {
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x03.toByte()) // 00000011

        val instruction = SRAIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x2E.toByte()),
            displacement = 0x00.toByte()
        )

        instruction.execute()

        assertEquals(0x01.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // 00000001 (shifted right, sign bit preserved as 0)
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag()) // Result 0x01 has odd parity
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry set to bit 0 (1)
    }

    @Test
    fun executeArithmeticShiftRightMemoryIXdNegativeWithCarry() {
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x85.toByte()) // 10000101

        val instruction = SRAIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x2E.toByte()),
            displacement = 0x00.toByte()
        )

        instruction.execute()

        assertEquals(0xC2.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // 11000010 (shifted right, sign bit preserved as 1)
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag()) // Result 0x01 has odd parity
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry set to bit 0 (1)
    }

    @Test
    fun executeArithmeticShiftRightMemoryIXdNegativeWithoutCarry() {
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x84.toByte()) // 10000100

        val instruction = SRAIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x2E.toByte()),
            displacement = 0x00.toByte()
        )

        instruction.execute()

        assertEquals(0xC2.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // 11000010 (shifted right, sign bit preserved as 1)
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag()) // Result 0x01 has odd parity
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag()) // Carry set to bit 0 (0)
    }

    @Test
    fun executeArithmeticShiftRightToZero() {
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x00.toByte())

        val instruction = SRAIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x2E.toByte()),
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
        Memory.memorySet.setMemoryCell(0x2000u, 0x06.toByte()) // 00000110

        val instruction = SRAIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x2E.toByte()),
            displacement = 0x00.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getPVFlag()) // Result 0x03 has even parity
        assertEquals(0x03.toByte(), Memory.memorySet.getMemoryCell(0x2000u))
    }

    @Test
    fun testParityOdd() {
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x02.toByte()) // 00000010

        val instruction = SRAIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x2E.toByte()),
            displacement = 0x00.toByte()
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getPVFlag()) // Result 0x01 has odd parity
        assertEquals(0x01.toByte(), Memory.memorySet.getMemoryCell(0x2000u))
    }

    @Test
    fun toStringFormat() {
        val instruction = SRAIXd(
            address = 0x0000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x2E.toByte()),
            displacement = 0x00.toByte()
        )

        assertEquals("SRA (IX + 0)", instruction.toString())
    }
}
