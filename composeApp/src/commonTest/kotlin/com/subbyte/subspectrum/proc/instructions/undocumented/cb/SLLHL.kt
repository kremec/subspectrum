package com.subbyte.subspectrum.proc.instructions.undocumented.cb

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.*

class SLLHLTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = SLLHL.decode(0xCB36L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xCB.toByte(), instruction.bytes[0])
        assertEquals(0x36.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeShiftLeftLogicalOneMemoryHLWithCarry() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x80.toByte())

        val instruction = SLLHL(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xCB.toByte(), 0x36.toByte()))
        )

        instruction.execute()

        assertEquals(0x01.toByte(), Memory.memorySet.getMemoryCell(0x2000u))
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag())
    }

    @Test
    fun executeShiftLeftLogicalOneMemoryHLWithoutCarry() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x40.toByte())

        val instruction = SLLHL(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xCB.toByte(), 0x36.toByte()))
        )

        instruction.execute()

        assertEquals(0x81.toByte(), Memory.memorySet.getMemoryCell(0x2000u))
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertTrue(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun executeShiftLeftLogicalOneFromZero() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x00.toByte())

        val instruction = SLLHL(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xCB.toByte(), 0x36.toByte()))
        )

        instruction.execute()

        assertEquals(0x01.toByte(), Memory.memorySet.getMemoryCell(0x2000u))
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun testParityEven() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x01.toByte())

        val instruction = SLLHL(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xCB.toByte(), 0x36.toByte()))
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getPVFlag())
        assertEquals(0x03.toByte(), Memory.memorySet.getMemoryCell(0x2000u))
    }

    @Test
    fun testParityOdd() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x09.toByte())

        val instruction = SLLHL(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xCB.toByte(), 0x36.toByte()))
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getPVFlag())
        assertEquals(0x13.toByte(), Memory.memorySet.getMemoryCell(0x2000u))
    }

    @Test
    fun toStringFormat() {
        val instruction = SLLHL(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xCB.toByte(), 0x36.toByte()))
        )

        assertEquals("SLL (HL)", instruction.toString())
    }
}
