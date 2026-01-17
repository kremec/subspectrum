package com.subbyte.subspectrum.proc.instructions.arith16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterPairCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ADDHLssTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ADDHLss.decode(0x09L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x09.toByte(), instruction.bytes[0])

        val addHlss = instruction as ADDHLss
        assertEquals(RegisterPairCode.BC, addHlss.sourceRegisterPairCode)
    }

    @Test
    fun executeAddHLBC() {
        Registers.registerSet.setHL(0x1000.toShort())
        Registers.registerSet.setBC(0x0F00.toShort())

        val instruction = ADDHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x1F00.toShort(), Registers.registerSet.getHL())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeAddHLDE() {
        Registers.registerSet.setHL(0x1000.toShort())
        Registers.registerSet.setDE(0x0F00.toShort())

        val instruction = ADDHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0x19.toByte()),
            sourceRegisterPairCode = RegisterPairCode.DE
        )

        instruction.execute()

        assertEquals(0x1F00.toShort(), Registers.registerSet.getHL())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeAddHLHL() {
        Registers.registerSet.setHL(0x1000.toShort())

        val instruction = ADDHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0x29.toByte()),
            sourceRegisterPairCode = RegisterPairCode.HL
        )

        instruction.execute()

        assertEquals(0x2000.toShort(), Registers.registerSet.getHL())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeAddHLSP() {
        Registers.registerSet.setHL(0x1000.toShort())
        Registers.specialPurposeRegisters.setSP(0x0F00.toShort())

        val instruction = ADDHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0x39.toByte()),
            sourceRegisterPairCode = RegisterPairCode.SP
        )

        instruction.execute()

        assertEquals(0x1F00.toShort(), Registers.registerSet.getHL())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun testHFlagSet() {
        // Test H flag set when carry from bit 11 (0xFFF + 0x001 = 0x1000 > 0xFFF)
        Registers.registerSet.setHL(0x0FFF.toShort()) // 0x0FFF & 0xFFF = 0xFFF
        Registers.registerSet.setBC(0x0001.toShort()) // 0x0001 & 0xFFF = 0x001

        val instruction = ADDHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x1000.toShort(), Registers.registerSet.getHL())
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun testHFlagReset() {
        // Test H flag reset when no carry from bit 11 (0x0FFE + 0x0001 = 0x0FFF <= 0xFFF)
        Registers.registerSet.setHL(0x0FFE.toShort()) // 0x0FFE & 0xFFF = 0xFFE
        Registers.registerSet.setBC(0x0001.toShort()) // 0x0001 & 0xFFF = 0x001

        val instruction = ADDHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x0FFF.toShort(), Registers.registerSet.getHL())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun testCFlagSet() {
        // Test C flag set when carry from bit 15, but H flag reset
        Registers.registerSet.setHL(0xF000.toShort()) // 0xF000 & 0xFFF = 0x000
        Registers.registerSet.setBC(0x1000.toShort()) // 0x1000 & 0xFFF = 0x000

        val instruction = ADDHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x0000.toShort(), Registers.registerSet.getHL())
        assertFalse(Registers.registerSet.getHFlag())
        assertTrue(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun testBothFlagsSet() {
        // Test both H and C flags set
        Registers.registerSet.setHL(0xFFFF.toShort()) // 0xFFFF & 0xFFF = 0xFFF
        Registers.registerSet.setBC(0xFFFF.toShort()) // 0xFFFF & 0xFFF = 0xFFF

        val instruction = ADDHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0xFFFE.toShort(), Registers.registerSet.getHL())
        assertTrue(Registers.registerSet.getHFlag())
        assertTrue(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = ADDHLss(
            address = 0x0000u,
            bytes = byteArrayOf(0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        assertEquals("ADD HL, BC", instruction.toString())
    }
}
