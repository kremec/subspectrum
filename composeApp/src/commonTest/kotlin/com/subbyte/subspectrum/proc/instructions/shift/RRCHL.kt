package com.subbyte.subspectrum.proc.instructions.shift

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RRCHLTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = RRCHL.decode(0xCB0EL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xCB.toByte(), instruction.bytes[0])
        assertEquals(0x0E.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeRotateRightMemoryHLWithCarry() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x01.toByte()) // 00000001

        val instruction = RRCHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x0E.toByte())
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
    fun executeRotateRightMemoryHLWithCarryFalse() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x02.toByte()) // 00000010

        val instruction = RRCHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x0E.toByte())
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
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x00.toByte())

        val instruction = RRCHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x0E.toByte())
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
        val instruction = RRCHL(
            address = 0x0000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x0E.toByte())
        )

        assertEquals("RRC (HL)", instruction.toString())
    }
}
