package com.subbyte.subspectrum.proc.instructions.jump

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JRNZdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val word = (0x20L shl 8) or 0x10L // 20 10: JR NZ, +16
        val instruction = JRNZd.decode(word, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0x20.toByte(), instruction.bytes[0])
        assertEquals(0x10.toByte(), instruction.bytes[1])

        val jrnzd = instruction as JRNZd
        assertEquals(0x10.toByte(), jrnzd.displacement)
    }

    @Test
    fun decodeInstructionNegativeDisplacement() {
        val word = (0x20L shl 8) or 0xF0L // 20 F0: JR NZ, -16
        val instruction = JRNZd.decode(word, 0x1000u)

        val jrnzd = instruction as JRNZd
        assertEquals(0xF0.toByte(), jrnzd.displacement) // -16 in two's complement
    }

    @Test
    fun executeJumpWhenZeroClear() {
        Registers.registerSet.setZFlag(false) // Zero flag clear

        val instruction = JRNZd(
            address = 0x1000u,
            bytes = byteArrayOf(0x20.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        instruction.execute()

        assertEquals(0x1010.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeNoJumpWhenZeroSet() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setZFlag(true) // Zero flag set

        val instruction = JRNZd(
            address = 0x1000u,
            bytes = byteArrayOf(0x20.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        instruction.execute()

        assertEquals(0x1000.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeJumpBackwardWhenZeroClear() {
        Registers.registerSet.setZFlag(false)

        val instruction = JRNZd(
            address = 0x1000u,
            bytes = byteArrayOf(0x20.toByte(), 0xF0.toByte()),
            displacement = 0xF0.toByte() // -16
        )

        instruction.execute()

        assertEquals(0x0FF0.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeNoJumpWhenZeroSetNegativeDisplacement() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setZFlag(true)

        val instruction = JRNZd(
            address = 0x1000u,
            bytes = byteArrayOf(0x20.toByte(), 0xF0.toByte()),
            displacement = 0xF0.toByte() // -16
        )

        instruction.execute()

        assertEquals(0x1000.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeNoFlagsAffectedWhenJumping() {
        Registers.registerSet.setZFlag(false)
        // Set other flags
        Registers.registerSet.setCFlag(true)
        Registers.registerSet.setNFlag(true)
        Registers.registerSet.setPVFlag(true)

        val instruction = JRNZd(
            address = 0x1000u,
            bytes = byteArrayOf(0x20.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        instruction.execute()

        // Z flag should remain clear, other flags unchanged
        assertTrue(!Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getPVFlag())
    }

    @Test
    fun executeNoFlagsAffectedWhenNotJumping() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setZFlag(true)
        // Set other flags
        Registers.registerSet.setCFlag(true)
        Registers.registerSet.setNFlag(true)
        Registers.registerSet.setPVFlag(true)

        val instruction = JRNZd(
            address = 0x1000u,
            bytes = byteArrayOf(0x20.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        instruction.execute()

        // Z flag should remain set, other flags unchanged
        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getPVFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = JRNZd(
            address = 0x0000u,
            bytes = byteArrayOf(0x20.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        assertEquals("JR NZ, +16", instruction.toString())
    }

    @Test
    fun toStringFormatNegative() {
        val instruction = JRNZd(
            address = 0x0000u,
            bytes = byteArrayOf(0x20.toByte(), 0xF0.toByte()),
            displacement = 0xF0.toByte() // -16
        )

        assertEquals("JR NZ, -16", instruction.toString())
    }
}