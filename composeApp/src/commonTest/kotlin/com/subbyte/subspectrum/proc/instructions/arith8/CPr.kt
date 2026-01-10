package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CPrTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = CPr.decode(0xB9L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0xB9.toByte(), instruction.bytes[0])

        val cpr = instruction as CPr
        assertEquals(RegisterCode.C, cpr.sourceRegister)
    }

    @Test
    fun executeCompareEqual() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setRegister(RegisterCode.C, 0x10.toByte())

        val instruction = CPr(
            address = 0x1000u,
            bytes = byteArrayOf(0xB9.toByte()),
            sourceRegister = RegisterCode.C
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertEquals(0x10.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun executeCompareNotEqual() {
        Registers.registerSet.setA(0x20.toByte())
        Registers.registerSet.setRegister(RegisterCode.C, 0x10.toByte())

        val instruction = CPr(
            address = 0x1000u,
            bytes = byteArrayOf(0xB9.toByte()),
            sourceRegister = RegisterCode.C
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertEquals(0x20.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun toStringFormat() {
        val instruction = CPr(
            address = 0x0000u,
            bytes = byteArrayOf(0xB9.toByte()),
            sourceRegister = RegisterCode.C
        )

        assertEquals("CP C", instruction.toString())
    }
}
