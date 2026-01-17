package com.subbyte.subspectrum.proc.instructions.arith16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterPairCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ADDIXssTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ADDIXss.decode(0xDD09L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0x09.toByte(), instruction.bytes[1])

        val addIxss = instruction as ADDIXss
        assertEquals(RegisterPairCode.BC, addIxss.sourceRegisterPairCode)
    }

    @Test
    fun executeAddIXBC() {
        Registers.specialPurposeRegisters.setIX(0x1000.toShort())
        Registers.registerSet.setBC(0x0F00.toShort())

        val instruction = ADDIXss(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x1F00.toShort(), Registers.specialPurposeRegisters.getIX())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeAddIXDE() {
        Registers.specialPurposeRegisters.setIX(0x1000.toShort())
        Registers.registerSet.setDE(0x0F00.toShort())

        val instruction = ADDIXss(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x19.toByte()),
            sourceRegisterPairCode = RegisterPairCode.DE
        )

        instruction.execute()

        assertEquals(0x1F00.toShort(), Registers.specialPurposeRegisters.getIX())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeAddIXHL() {
        Registers.specialPurposeRegisters.setIX(0x1000.toShort())
        Registers.registerSet.setHL(0x0F00.toShort())

        val instruction = ADDIXss(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x29.toByte()),
            sourceRegisterPairCode = RegisterPairCode.HL
        )

        instruction.execute()

        assertEquals(0x1F00.toShort(), Registers.specialPurposeRegisters.getIX())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeAddIXSP() {
        Registers.specialPurposeRegisters.setIX(0x1000.toShort())
        Registers.specialPurposeRegisters.setSP(0x0F00.toShort())

        val instruction = ADDIXss(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x39.toByte()),
            sourceRegisterPairCode = RegisterPairCode.SP
        )

        instruction.execute()

        assertEquals(0x1F00.toShort(), Registers.specialPurposeRegisters.getIX())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun testHFlagSet() {
        // Test H flag set when carry from bit 11
        Registers.specialPurposeRegisters.setIX(0x0FFF.toShort()) // 0x0FFF & 0xFFF = 0xFFF
        Registers.registerSet.setBC(0x0001.toShort())            // 0x0001 & 0xFFF = 0x001

        val instruction = ADDIXss(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x1000.toShort(), Registers.specialPurposeRegisters.getIX())
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun testHFlagReset() {
        // Test H flag reset when no carry from bit 11
        Registers.specialPurposeRegisters.setIX(0x0FFE.toShort()) // 0x0FFE & 0xFFF = 0xFFE
        Registers.registerSet.setBC(0x0001.toShort())            // 0x0001 & 0xFFF = 0x001

        val instruction = ADDIXss(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x0FFF.toShort(), Registers.specialPurposeRegisters.getIX())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun testCFlagSet() {
        // Test C flag set when carry from bit 15, H flag reset
        Registers.specialPurposeRegisters.setIX(0xF000.toShort()) // 0xF000 & 0xFFF = 0x000
        Registers.registerSet.setBC(0x1000.toShort())            // 0x1000 & 0xFFF = 0x000

        val instruction = ADDIXss(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x0000.toShort(), Registers.specialPurposeRegisters.getIX())
        assertFalse(Registers.registerSet.getHFlag())
        assertTrue(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun testCFlagReset() {
        // Test C flag reset when no carry from bit 15
        Registers.specialPurposeRegisters.setIX(0xFFFE.toShort())
        Registers.registerSet.setBC(0x0001.toShort())

        val instruction = ADDIXss(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0xFFFF.toShort(), Registers.specialPurposeRegisters.getIX())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun testBothFlagsSet() {
        // Test both H and C flags set
        Registers.specialPurposeRegisters.setIX(0xFFFF.toShort()) // 0xFFFF & 0xFFF = 0xFFF
        Registers.registerSet.setBC(0xFFFF.toShort())            // 0xFFFF & 0xFFF = 0xFFF

        val instruction = ADDIXss(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0xFFFE.toShort(), Registers.specialPurposeRegisters.getIX())
        assertTrue(Registers.registerSet.getHFlag())
        assertTrue(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = ADDIXss(
            address = 0x0000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        assertEquals("ADD IX, BC", instruction.toString())
    }
}
