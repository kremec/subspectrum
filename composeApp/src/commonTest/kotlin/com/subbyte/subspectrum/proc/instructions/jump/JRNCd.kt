package com.subbyte.subspectrum.proc.instructions.jump

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JRNCdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val word = (0x30L shl 8) or 0x10L // 30 10: JR NC, +16
        val instruction = JRNCd.decode(word, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0x30.toByte(), instruction.bytes[0])
        assertEquals(0x10.toByte(), instruction.bytes[1])

        val jrncd = instruction as JRNCd
        assertEquals(0x10.toByte(), jrncd.displacement)
    }

    @Test
    fun decodeInstructionNegativeDisplacement() {
        val word = (0x30L shl 8) or 0xF0L // 30 F0: JR NC, -16
        val instruction = JRNCd.decode(word, 0x1000u)

        val jrncd = instruction as JRNCd
        assertEquals(0xF0.toByte(), jrncd.displacement) // -16 in two's complement
    }

    @Test
    fun executeJumpWhenCarryClear() {
        Registers.registerSet.setCFlag(false) // Carry flag clear

        val instruction = JRNCd(
            address = 0x1000u,
            bytes = byteArrayOf(0x30.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        instruction.execute()

        assertEquals(0x1010.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeNoJumpWhenCarrySet() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setCFlag(true) // Carry flag set

        val instruction = JRNCd(
            address = 0x1000u,
            bytes = byteArrayOf(0x30.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        instruction.execute()

        assertEquals(0x1000.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeJumpBackwardWhenCarryClear() {
        Registers.registerSet.setCFlag(false)

        val instruction = JRNCd(
            address = 0x1000u,
            bytes = byteArrayOf(0x30.toByte(), 0xF0.toByte()),
            displacement = 0xF0.toByte() // -16
        )

        instruction.execute()

        assertEquals(0x0FF0.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeNoJumpWhenCarrySetNegativeDisplacement() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setCFlag(true)

        val instruction = JRNCd(
            address = 0x1000u,
            bytes = byteArrayOf(0x30.toByte(), 0xF0.toByte()),
            displacement = 0xF0.toByte() // -16
        )

        instruction.execute()

        assertEquals(0x1000.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeNoFlagsAffectedWhenJumping() {
        Registers.registerSet.setCFlag(false)
        // Set other flags
        Registers.registerSet.setZFlag(true)
        Registers.registerSet.setNFlag(true)
        Registers.registerSet.setPVFlag(true)

        val instruction = JRNCd(
            address = 0x1000u,
            bytes = byteArrayOf(0x30.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        instruction.execute()

        // C flag should remain clear, other flags unchanged
        assertTrue(!Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getPVFlag())
    }

    @Test
    fun executeNoFlagsAffectedWhenNotJumping() {
        Registers.registerSet.setCFlag(true)
        // Set other flags
        Registers.registerSet.setZFlag(true)
        Registers.registerSet.setNFlag(true)
        Registers.registerSet.setPVFlag(true)

        val instruction = JRNCd(
            address = 0x1000u,
            bytes = byteArrayOf(0x30.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        instruction.execute()

        // C flag should remain set, other flags unchanged
        assertTrue(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getPVFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = JRNCd(
            address = 0x0000u,
            bytes = byteArrayOf(0x30.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        assertEquals("JR NC, +16", instruction.toString())
    }

    @Test
    fun toStringFormatNegative() {
        val instruction = JRNCd(
            address = 0x0000u,
            bytes = byteArrayOf(0x30.toByte(), 0xF0.toByte()),
            displacement = 0xF0.toByte() // -16
        )

        assertEquals("JR NC, -16", instruction.toString())
    }
}