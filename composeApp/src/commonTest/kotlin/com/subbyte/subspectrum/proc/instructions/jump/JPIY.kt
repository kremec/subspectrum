package com.subbyte.subspectrum.proc.instructions.jump

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JPIYTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val word = 0xFDE9L // FD E9: JP (IY)
        val instruction = JPIY.decode(word, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xFD.toByte(), instruction.bytes[0])
        assertEquals(0xE9.toByte(), instruction.bytes[1])

        assertTrue(instruction is JPIY)
    }

    @Test
    fun executeJumpToIY() {
        Registers.specialPurposeRegisters.setIY(0x5678)

        val instruction = JPIY(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xE9.toByte())
        )

        instruction.execute()

        assertEquals(0x5678.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeJumpToZero() {
        Registers.specialPurposeRegisters.setIY(0x0000)

        val instruction = JPIY(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xE9.toByte())
        )

        instruction.execute()

        assertEquals(0x0000.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeJumpToHighAddress() {
        Registers.specialPurposeRegisters.setIY(0xFFFF.toShort())

        val instruction = JPIY(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xE9.toByte())
        )

        instruction.execute()

        assertEquals(0xFFFF.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeNoFlagsAffected() {
        Registers.specialPurposeRegisters.setIY(0x5678)
        // Set some flags
        Registers.registerSet.setZFlag(true)
        Registers.registerSet.setCFlag(true)
        Registers.registerSet.setNFlag(true)
        Registers.registerSet.setPVFlag(true)

        val instruction = JPIY(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xE9.toByte())
        )

        instruction.execute()

        // All flags should remain unchanged
        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getPVFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = JPIY(
            address = 0x0000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xE9.toByte())
        )

        assertEquals("JP (IY)", instruction.toString())
    }
}