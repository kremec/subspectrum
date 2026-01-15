package com.subbyte.subspectrum.proc.instructions.shift

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RRCIXdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = RRCIXd.decode(0xDDCB000EL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(4, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0xCB.toByte(), instruction.bytes[1])
        assertEquals(0x00.toByte(), instruction.bytes[2])
        assertEquals(0x0E.toByte(), instruction.bytes[3])

        val rrcIXd = instruction as RRCIXd
        assertEquals(0x00.toByte(), rrcIXd.displacement)
    }

    @Test
    fun executeRotateRightMemoryIXdWithCarry() {
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x01.toByte()) // 00000001

        val instruction = RRCIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x0E.toByte()),
            displacement = 0x00.toByte()
        )

        instruction.execute()

        assertEquals(0x80.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // 10000000 (bit 0 becomes bit 7)
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry set to bit 0
    }

    @Test
    fun executeRotateRightMemoryIXdWithCarryFalse() {
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x02.toByte()) // 00000010

        val instruction = RRCIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x0E.toByte()),
            displacement = 0x00.toByte()
        )

        instruction.execute()

        assertEquals(0x01.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // 00000001 (shifted right, bit 0 becomes bit 7)
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag()) // Carry not set because bit 0 was 0
    }

    @Test
    fun executeRotateToZero() {
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x00.toByte())

        val instruction = RRCIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x0E.toByte()),
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
        val instruction = RRCIXd(
            address = 0x0000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x0E.toByte()),
            displacement = 0x00.toByte()
        )

        assertEquals("RRC (IX + 0)", instruction.toString())
    }
}
