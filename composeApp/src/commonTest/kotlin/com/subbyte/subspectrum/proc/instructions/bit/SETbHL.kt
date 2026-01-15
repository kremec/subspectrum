package com.subbyte.subspectrum.proc.instructions.bit

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SETbHLTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = SETbHL.decode(0xCBC6L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xCB.toByte(), instruction.bytes[0])
        assertEquals(0xC6.toByte(), instruction.bytes[1])

        val setbHL = instruction as SETbHL
        assertEquals(0, setbHL.bit)
    }

    @Test
    fun decodeInstructionBit7() {
        val instruction = SETbHL.decode(0xCBFFL, 0x1000u)

        val setbHL = instruction as SETbHL
        assertEquals(7, setbHL.bit)
    }

    @Test
    fun executeSetBit() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x00.toByte()) // bit 0 clear

        val instruction = SETbHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0xC6.toByte()),
            bit = 0
        )

        instruction.execute()

        assertEquals(0x01.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // bit 0 set
    }

    @Test
    fun executeSetBitAlreadySet() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x01.toByte()) // bit 0 already set

        val instruction = SETbHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0xC6.toByte()),
            bit = 0
        )

        instruction.execute()

        assertEquals(0x01.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // bit 0 still set
    }

    @Test
    fun executeSetBit7() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x7F.toByte()) // bit 7 clear

        val instruction = SETbHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0xFE.toByte()),
            bit = 7
        )

        instruction.execute()

        assertEquals(0xFF.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // bit 7 set
    }

    @Test
    fun executeSetBitNoFlagsAffected() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x00.toByte())
        // Set some flags
        Registers.registerSet.setZFlag(true)
        Registers.registerSet.setCFlag(true)
        Registers.registerSet.setNFlag(true)

        val instruction = SETbHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0xC6.toByte()),
            bit = 0
        )

        instruction.execute()

        // Flags should remain unchanged
        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeSetDifferentBits() {
        Registers.registerSet.setHL(0x2000.toShort())

        for (bit in 0..7) {
            Memory.memorySet.setMemoryCell(0x2000u, 0x00.toByte())

            val instruction = SETbHL(
                address = 0x1000u,
                bytes = byteArrayOf(0xCB.toByte(), (0xC6 or (bit shl 3)).toByte()),
                bit = bit
            )

            instruction.execute()

            val expectedValue = (1 shl bit).toByte()
            assertEquals(expectedValue, Memory.memorySet.getMemoryCell(0x2000u))
        }
    }

    @Test
    fun toStringFormat() {
        val instruction = SETbHL(
            address = 0x0000u,
            bytes = byteArrayOf(0xCB.toByte(), 0xC6.toByte()),
            bit = 0
        )

        assertEquals("SET 0, (HL)", instruction.toString())
    }
}
