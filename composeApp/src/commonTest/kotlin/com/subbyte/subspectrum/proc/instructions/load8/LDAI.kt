package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LDAITest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDAI.decode(0xED57L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0x57.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeLoadIToA() {
        Registers.specialPurposeRegisters.setI(0xAB.toByte())
        Registers.registerSet.setA(0x00.toByte())

        val instruction = LDAI(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x57.toByte())
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeLoadIToAWithNegativeValue() {
        Registers.specialPurposeRegisters.setI((-1).toByte())
        Registers.registerSet.setA(0x00.toByte())

        val instruction = LDAI(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x57.toByte())
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = LDAI(
            address = 0x0000u,
            bytes = byteArrayOf(0xED.toByte(), 0x57.toByte())
        )

        assertEquals("LD A, I", instruction.toString())
    }
}
