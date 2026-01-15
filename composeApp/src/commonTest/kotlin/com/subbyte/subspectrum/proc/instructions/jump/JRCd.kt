package com.subbyte.subspectrum.proc.instructions.jump

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JRCdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val word = (0x38L shl 8) or 0x10L // 38 10: JR C, +16
        val instruction = JRCd.decode(word, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0x38.toByte(), instruction.bytes[0])
        assertEquals(0x10.toByte(), instruction.bytes[1])

        val jrcd = instruction as JRCd
        assertEquals(0x10.toByte(), jrcd.displacement)
    }

    @Test
    fun decodeInstructionNegativeDisplacement() {
        val word = (0x38L shl 8) or 0xF0L // 38 F0: JR C, -16
        val instruction = JRCd.decode(word, 0x1000u)

        val jrcd = instruction as JRCd
        assertEquals(0xF0.toByte(), jrcd.displacement) // -16 in two's complement
    }

    @Test
    fun executeJumpWhenCarrySet() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setCFlag(true) // Carry flag set

        val instruction = JRCd(
            address = 0x1000u,
            bytes = byteArrayOf(0x38.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        instruction.execute()

        assertEquals(0x1010.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeNoJumpWhenCarryClear() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setCFlag(false) // Carry flag clear

        val instruction = JRCd(
            address = 0x1000u,
            bytes = byteArrayOf(0x38.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        instruction.execute()

        assertEquals(0x1000.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeJumpBackwardWhenCarrySet() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setCFlag(true)

        val instruction = JRCd(
            address = 0x1000u,
            bytes = byteArrayOf(0x38.toByte(), 0xF0.toByte()),
            displacement = 0xF0.toByte() // -16
        )

        instruction.execute()

        assertEquals(0x0FF0.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeNoJumpWhenCarryClearNegativeDisplacement() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setCFlag(false)

        val instruction = JRCd(
            address = 0x1000u,
            bytes = byteArrayOf(0x38.toByte(), 0xF0.toByte()),
            displacement = 0xF0.toByte() // -16
        )

        instruction.execute()

        assertEquals(0x1000.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeNoFlagsAffectedWhenJumping() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setCFlag(true)
        // Set other flags
        Registers.registerSet.setZFlag(true)
        Registers.registerSet.setNFlag(true)
        Registers.registerSet.setPVFlag(true)

        val instruction = JRCd(
            address = 0x1000u,
            bytes = byteArrayOf(0x38.toByte(), 0x10.toByte()),
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
    fun executeNoFlagsAffectedWhenNotJumping() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setCFlag(false)
        // Set other flags
        Registers.registerSet.setZFlag(true)
        Registers.registerSet.setNFlag(true)
        Registers.registerSet.setPVFlag(true)

        val instruction = JRCd(
            address = 0x1000u,
            bytes = byteArrayOf(0x38.toByte(), 0x10.toByte()),
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
    fun toStringFormat() {
        val instruction = JRCd(
            address = 0x0000u,
            bytes = byteArrayOf(0x38.toByte(), 0x10.toByte()),
            displacement = 0x10
        )

        assertEquals("JR C, +16", instruction.toString())
    }

    @Test
    fun toStringFormatNegative() {
        val instruction = JRCd(
            address = 0x0000u,
            bytes = byteArrayOf(0x38.toByte(), 0xF0.toByte()),
            displacement = 0xF0.toByte() // -16
        )

        assertEquals("JR C, -16", instruction.toString())
    }
}