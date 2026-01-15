package com.subbyte.subspectrum.proc.instructions.bit

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BITbHLTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = BITbHL.decode(0xCB46L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xCB.toByte(), instruction.bytes[0])
        assertEquals(0x46.toByte(), instruction.bytes[1])

        val bitbHL = instruction as BITbHL
        assertEquals(0, bitbHL.bit)
    }

    @Test
    fun executeBitTestMemoryBitSet() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x01.toByte()) // bit 0 set

        val instruction = BITbHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x46.toByte()),
            bit = 0
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getZFlag()) // Z=0 because bit is set
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeBitTestMemoryBitClear() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0xFE.toByte()) // bit 0 clear

        val instruction = BITbHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x46.toByte()),
            bit = 0
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getZFlag()) // Z=1 because bit is clear
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeBitTestMemoryBit7() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x80.toByte()) // bit 7 set

        val instruction = BITbHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x7E.toByte()),
            bit = 7
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getZFlag()) // Z=0 because bit 7 is set
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = BITbHL(
            address = 0x0000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x46.toByte()),
            bit = 0
        )

        assertEquals("BIT 0, (HL)", instruction.toString())
    }
}