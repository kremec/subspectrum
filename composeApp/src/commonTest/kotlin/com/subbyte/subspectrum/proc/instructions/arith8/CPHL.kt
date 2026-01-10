package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CPHLTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = CPHL.decode(0xBEL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0xBE.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeCompareWithMemoryHL() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x10.toByte())

        val instruction = CPHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xBE.toByte())
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertEquals(0x10.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun toStringFormat() {
        val instruction = CPHL(
            address = 0x0000u,
            bytes = byteArrayOf(0xBE.toByte())
        )

        assertEquals("CP (HL)", instruction.toString())
    }
}
