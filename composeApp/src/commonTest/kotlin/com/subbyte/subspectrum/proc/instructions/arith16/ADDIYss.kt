package com.subbyte.subspectrum.proc.instructions.arith16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterPairCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ADDIYssTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ADDIYss.decode(0xFD09L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xFD.toByte(), instruction.bytes[0])
        assertEquals(0x09.toByte(), instruction.bytes[1])

        val addIyss = instruction as ADDIYss
        assertEquals(RegisterPairCode.BC, addIyss.sourceRegisterPairCode)
    }

    @Test
    fun executeAddIYBC() {
        Registers.specialPurposeRegisters.setIY(0x1000.toShort())
        Registers.registerSet.setBC(0x0F00.toShort())

        val instruction = ADDIYss(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x1F00.toShort(), Registers.specialPurposeRegisters.getIY())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeAddIYDE() {
        Registers.specialPurposeRegisters.setIY(0x1000.toShort())
        Registers.registerSet.setDE(0x0F00.toShort())

        val instruction = ADDIYss(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x19.toByte()),
            sourceRegisterPairCode = RegisterPairCode.DE
        )

        instruction.execute()

        assertEquals(0x1F00.toShort(), Registers.specialPurposeRegisters.getIY())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeAddIYHL() {
        Registers.specialPurposeRegisters.setIY(0x1000.toShort())
        Registers.registerSet.setHL(0x0F00.toShort())

        val instruction = ADDIYss(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x29.toByte()),
            sourceRegisterPairCode = RegisterPairCode.HL
        )

        instruction.execute()

        assertEquals(0x1F00.toShort(), Registers.specialPurposeRegisters.getIY())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeAddIYSP() {
        Registers.specialPurposeRegisters.setIY(0x1000.toShort())
        Registers.specialPurposeRegisters.setSP(0x0F00.toShort())

        val instruction = ADDIYss(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x39.toByte()),
            sourceRegisterPairCode = RegisterPairCode.SP
        )

        instruction.execute()

        assertEquals(0x1F00.toShort(), Registers.specialPurposeRegisters.getIY())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun testHFlagSet() {
        // Test H flag set when carry from bit 11
        Registers.specialPurposeRegisters.setIY(0x0FFF.toShort()) // 0x0FFF & 0xFFF = 0xFFF
        Registers.registerSet.setBC(0x0001.toShort())            // 0x0001 & 0xFFF = 0x001

        val instruction = ADDIYss(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x1000.toShort(), Registers.specialPurposeRegisters.getIY())
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun testHFlagReset() {
        // Test H flag reset when no carry from bit 11
        Registers.specialPurposeRegisters.setIY(0x0FFE.toShort()) // 0x0FFE & 0xFFF = 0xFFE
        Registers.registerSet.setBC(0x0001.toShort())            // 0x0001 & 0xFFF = 0x001

        val instruction = ADDIYss(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x0FFF.toShort(), Registers.specialPurposeRegisters.getIY())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun testCFlagSet() {
        // Test C flag set when carry from bit 15, H flag reset
        Registers.specialPurposeRegisters.setIY(0xF000.toShort()) // 0xF000 & 0xFFF = 0x000
        Registers.registerSet.setBC(0x1000.toShort())            // 0x1000 & 0xFFF = 0x000

        val instruction = ADDIYss(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x0000.toShort(), Registers.specialPurposeRegisters.getIY())
        assertFalse(Registers.registerSet.getHFlag())
        assertTrue(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun testCFlagReset() {
        // Test C flag reset when no carry from bit 15
        Registers.specialPurposeRegisters.setIY(0xFFFE.toShort())
        Registers.registerSet.setBC(0x0001.toShort())

        val instruction = ADDIYss(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0xFFFF.toShort(), Registers.specialPurposeRegisters.getIY())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun testBothFlagsSet() {
        // Test both H and C flags set
        Registers.specialPurposeRegisters.setIY(0xFFFF.toShort()) // 0xFFFF & 0xFFF = 0xFFF
        Registers.registerSet.setBC(0xFFFF.toShort())            // 0xFFFF & 0xFFF = 0xFFF

        val instruction = ADDIYss(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0xFFFE.toShort(), Registers.specialPurposeRegisters.getIY())
        assertTrue(Registers.registerSet.getHFlag())
        assertTrue(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = ADDIYss(
            address = 0x0000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        assertEquals("ADD IY, BC", instruction.toString())
    }
}
