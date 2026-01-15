package com.subbyte.subspectrum.proc.instructions.jump

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DJNZdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val word = (0x10L shl 8) or 0x05L // 10 05: DJNZ +5
        val instruction = DJNZd.decode(word, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0x10.toByte(), instruction.bytes[0])
        assertEquals(0x05.toByte(), instruction.bytes[1])

        val djnzd = instruction as DJNZd
        assertEquals(0x05.toByte(), djnzd.displacement)
    }

    @Test
    fun decodeInstructionNegativeDisplacement() {
        val word = (0x10L shl 8) or 0xFBL // 10 FB: DJNZ -5
        val instruction = DJNZd.decode(word, 0x1000u)

        val djnzd = instruction as DJNZd
        assertEquals(0xFB.toByte(), djnzd.displacement) // -5 in two's complement
    }

    @Test
    fun executeDecrementAndJumpWhenBNotZero() {
        Registers.registerSet.setB(0x05) // B = 5
        Registers.specialPurposeRegisters.setPC(0x1000)

        val instruction = DJNZd(
            address = 0x1000u,
            bytes = byteArrayOf(0x10.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        instruction.execute()

        assertEquals(0x04.toByte(), Registers.registerSet.getB()) // B should be decremented to 4
        assertEquals(0x1010.toShort(), Registers.specialPurposeRegisters.getPC()) // Should jump +16
    }

    @Test
    fun executeDecrementAndNoJumpWhenBBecomesZero() {
        Registers.registerSet.setB(0x01) // B = 1
        Registers.specialPurposeRegisters.setPC(0x1000)

        val instruction = DJNZd(
            address = 0x1000u,
            bytes = byteArrayOf(0x10.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getB()) // B should be decremented to 0
        assertEquals(0x1000.toShort(), Registers.specialPurposeRegisters.getPC()) // Should not jump
    }

    @Test
    fun executeDecrementAndJumpBackward() {
        Registers.registerSet.setB(0x03) // B = 3

        val instruction = DJNZd(
            address = 0x1000u,
            bytes = byteArrayOf(0x10.toByte(), 0xF0.toByte()),
            displacement = 0xF0.toByte() // -16
        )

        instruction.execute()

        assertEquals(0x02.toByte(), Registers.registerSet.getB()) // B decremented to 2
        assertEquals(0x0FF0.toShort(), Registers.specialPurposeRegisters.getPC()) // Jump -16 from 0x1000
    }

    @Test
    fun executeDecrementFromZero() {
        Registers.registerSet.setB(0x00) // B = 0
        Registers.specialPurposeRegisters.setPC(0x1000)

        val instruction = DJNZd(
            address = 0x1000u,
            bytes = byteArrayOf(0x10.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        instruction.execute()

        assertEquals(0xFF.toByte(), Registers.registerSet.getB()) // B wraps around: 0 - 1 = 255
        assertEquals(0x1010.toShort(), Registers.specialPurposeRegisters.getPC()) // Should jump since 255 != 0
    }

    @Test
    fun executeDecrementFromOne() {
        Registers.registerSet.setB(0x01) // B = 1
        Registers.specialPurposeRegisters.setPC(0x1000)

        val instruction = DJNZd(
            address = 0x1000u,
            bytes = byteArrayOf(0x10.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getB()) // B decremented to 0
        assertEquals(0x1000.toShort(), Registers.specialPurposeRegisters.getPC()) // No jump when B becomes 0
    }

    @Test
    fun executeNoFlagsAffected() {
        Registers.registerSet.setB(0x05)
        // Set some flags
        Registers.registerSet.setZFlag(true)
        Registers.registerSet.setCFlag(true)
        Registers.registerSet.setNFlag(true)
        Registers.registerSet.setPVFlag(true)

        val instruction = DJNZd(
            address = 0x1000u,
            bytes = byteArrayOf(0x10.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        instruction.execute()

        // All flags should remain unchanged
        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getPVFlag())
    }

    @Test
    fun executeDecrementOnlyAffectsB() {
        Registers.registerSet.setA(0xAA.toByte())
        Registers.registerSet.setB(0x05)
        Registers.registerSet.setC(0xBB.toByte())
        Registers.registerSet.setD(0xCC.toByte())

        val instruction = DJNZd(
            address = 0x1000u,
            bytes = byteArrayOf(0x10.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        instruction.execute()

        // Only B should change
        assertEquals(0xAA.toByte(), Registers.registerSet.getA())
        assertEquals(0x04.toByte(), Registers.registerSet.getB())
        assertEquals(0xBB.toByte(), Registers.registerSet.getC())
        assertEquals(0xCC.toByte(), Registers.registerSet.getD())
    }

    @Test
    fun toStringFormat() {
        val instruction = DJNZd(
            address = 0x0000u,
            bytes = byteArrayOf(0x10.toByte(), 0x05.toByte()),
            displacement = 0x05
        )

        assertEquals("DJNZ +5", instruction.toString())
    }

    @Test
    fun toStringFormatNegative() {
        val instruction = DJNZd(
            address = 0x0000u,
            bytes = byteArrayOf(0x10.toByte(), 0xFB.toByte()),
            displacement = 0xFB.toByte() // -5
        )

        assertEquals("DJNZ -5", instruction.toString())
    }
}