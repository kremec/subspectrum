package com.subbyte.subspectrum.proc.instructions.shift

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RLIYdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = RLIYd.decode(0xFDCB0016L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(4, instruction.bytes.size)
        assertEquals(0xFD.toByte(), instruction.bytes[0])
        assertEquals(0xCB.toByte(), instruction.bytes[1])
        assertEquals(0x00.toByte(), instruction.bytes[2])
        assertEquals(0x16.toByte(), instruction.bytes[3])

        val rlIYd = instruction as RLIYd
        assertEquals(0x00.toByte(), rlIYd.displacement)
    }

    @Test
    fun executeRotateLeftMemoryIYdWithCarry() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x80.toByte()) // 10000000
        Registers.registerSet.setCFlag(true)

        val instruction = RLIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x16.toByte()),
            displacement = 0x00.toByte()
        )

        instruction.execute()

        assertEquals(0x01.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // 00000001 (bit 0 set to old carry)
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry set to bit 7
    }

    @Test
    fun executeRotateLeftMemoryIYdWithCarryFalse() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x80.toByte()) // 10000000
        Registers.registerSet.setCFlag(false)

        val instruction = RLIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x16.toByte()),
            displacement = 0x00.toByte()
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // 00000000 (bit 0 not set because carry was false)
        assertFalse(Registers.registerSet.getSFlag())
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry set to bit 7
    }

    @Test
    fun executeRotateLeftMemoryIYdWithNoCarryInBit7() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x40.toByte()) // 01000000
        Registers.registerSet.setCFlag(true)

        val instruction = RLIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x16.toByte()),
            displacement = 0x00.toByte()
        )

        instruction.execute()

        assertEquals(0x81.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // 10000001
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag()) // Carry not set because bit 7 was 0
    }

    @Test
    fun executeRotateToZero() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x00.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = RLIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x16.toByte()),
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
        val instruction = RLIYd(
            address = 0x0000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x00.toByte(), 0x16.toByte()),
            displacement = 0x00.toByte()
        )

        assertEquals("RL (IY + 0)", instruction.toString())
    }
}
