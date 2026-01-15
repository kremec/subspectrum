package com.subbyte.subspectrum.proc.instructions.jump

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JRdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val word = (0x18L shl 8) or 0x10L // 18 10: JR 10h
        val instruction = JRd.decode(word, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0x18.toByte(), instruction.bytes[0])
        assertEquals(0x10.toByte(), instruction.bytes[1])

        val jrd = instruction as JRd
        assertEquals(0x10.toByte(), jrd.displacement)
    }

    @Test
    fun decodeInstructionNegativeDisplacement() {
        val word = (0x18L shl 8) or 0xF0L // 18 F0: JR -16
        val instruction = JRd.decode(word, 0x1000u)

        val jrd = instruction as JRd
        assertEquals(0xF0.toByte(), jrd.displacement) // -16 in two's complement
    }

    @Test
    fun executeJumpForward() {
        Registers.specialPurposeRegisters.setPC(0x1000)

        val instruction = JRd(
            address = 0x1000u,
            bytes = byteArrayOf(0x18.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        instruction.execute()

        assertEquals(0x1010.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeJumpBackward() {
        Registers.specialPurposeRegisters.setPC(0x1000)

        val instruction = JRd(
            address = 0x1000u,
            bytes = byteArrayOf(0x18.toByte(), 0xF0.toByte()),
            displacement = 0xF0.toByte() // -16
        )

        instruction.execute()

        assertEquals(0x0FF0.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeJumpZeroDisplacement() {
        Registers.specialPurposeRegisters.setPC(0x1000)

        val instruction = JRd(
            address = 0x1000u,
            bytes = byteArrayOf(0x18.toByte(), 0x00.toByte()),
            displacement = 0x00
        )

        instruction.execute()

        assertEquals(0x1000.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeJumpMaximumPositive() {
        Registers.specialPurposeRegisters.setPC(0x1000)

        val instruction = JRd(
            address = 0x1000u,
            bytes = byteArrayOf(0x18.toByte(), 0x7F.toByte()),
            displacement = 0x7F
        )

        instruction.execute()

        assertEquals(0x107F.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeJumpMaximumNegative() {
        Registers.specialPurposeRegisters.setPC(0x1000)

        val instruction = JRd(
            address = 0x1000u,
            bytes = byteArrayOf(0x18.toByte(), 0x80.toByte()),
            displacement = 0x80.toByte() // -128
        )

        instruction.execute()

        assertEquals(0x0F80.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeNoFlagsAffected() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        // Set some flags
        Registers.registerSet.setZFlag(true)
        Registers.registerSet.setCFlag(true)
        Registers.registerSet.setNFlag(true)

        val instruction = JRd(
            address = 0x1000u,
            bytes = byteArrayOf(0x18.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        instruction.execute()

        // Flags should remain unchanged
        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = JRd(
            address = 0x0000u,
            bytes = byteArrayOf(0x18.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        assertEquals("JR +16", instruction.toString())
    }

    @Test
    fun toStringFormatNegative() {
        val instruction = JRd(
            address = 0x0000u,
            bytes = byteArrayOf(0x18.toByte(), 0xF0.toByte()),
            displacement = 0xF0.toByte() // -16
        )

        assertEquals("JR -16", instruction.toString())
    }
}