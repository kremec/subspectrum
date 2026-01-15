package com.subbyte.subspectrum.proc.instructions.bit

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RESbHLTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = RESbHL.decode(0xCB86L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xCB.toByte(), instruction.bytes[0])
        assertEquals(0x86.toByte(), instruction.bytes[1])

        val resbHL = instruction as RESbHL
        assertEquals(0, resbHL.bit)
    }

    @Test
    fun decodeInstructionBit7() {
        val instruction = RESbHL.decode(0xCBBEL, 0x1000u)

        val resbHL = instruction as RESbHL
        assertEquals(7, resbHL.bit)
    }

    @Test
    fun executeResetBit() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x01.toByte()) // bit 0 set

        val instruction = RESbHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x86.toByte()),
            bit = 0
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // bit 0 cleared
    }

    @Test
    fun executeResetBitAlreadyClear() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x00.toByte()) // bit 0 already clear

        val instruction = RESbHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x86.toByte()),
            bit = 0
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // bit 0 still clear
    }

    @Test
    fun executeResetBit7() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0xFF.toByte()) // bit 7 set

        val instruction = RESbHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0xBE.toByte()),
            bit = 7
        )

        instruction.execute()

        assertEquals(0x7F.toByte(), Memory.memorySet.getMemoryCell(0x2000u)) // bit 7 cleared
    }

    @Test
    fun executeResetBitNoFlagsAffected() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x01.toByte())
        // Set some flags
        Registers.registerSet.setZFlag(true)
        Registers.registerSet.setCFlag(true)
        Registers.registerSet.setNFlag(true)

        val instruction = RESbHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x86.toByte()),
            bit = 0
        )

        instruction.execute()

        // Flags should remain unchanged
        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeResetDifferentBits() {
        Registers.registerSet.setHL(0x2000.toShort())

        for (bit in 0..7) {
            Memory.memorySet.setMemoryCell(0x2000u, 0xFF.toByte())

            val instruction = RESbHL(
                address = 0x1000u,
                bytes = byteArrayOf(0xCB.toByte(), (0x86 or (bit shl 3)).toByte()),
                bit = bit
            )

            instruction.execute()

            val expectedValue = (0xFF and (1 shl bit).inv()).toByte()
            assertEquals(expectedValue, Memory.memorySet.getMemoryCell(0x2000u))
        }
    }

    @Test
    fun toStringFormat() {
        val instruction = RESbHL(
            address = 0x0000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x86.toByte()),
            bit = 0
        )

        assertEquals("RES 0, (HL)", instruction.toString())
    }
}
