package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ADDAnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ADDAn.decode(0xC6ABL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xC6.toByte(), instruction.bytes[0])
        assertEquals(0xAB.toByte(), instruction.bytes[1])

        val addAn = instruction as ADDAn
        assertEquals(0xAB.toByte(), addAn.sourceByte)
    }

    @Test
    fun executeAddImmediate() {
        Registers.registerSet.setA(0x10.toByte())

        val instruction = ADDAn(
            address = 0x1000u,
            bytes = byteArrayOf(0xC6.toByte(), 0x20.toByte()),
            sourceByte = 0x20.toByte()
        )

        instruction.execute()

        assertEquals(0x30.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = ADDAn(
            address = 0x0000u,
            bytes = byteArrayOf(0xC6.toByte(), 0xAB.toByte()),
            sourceByte = 0xAB.toByte()
        )

        assertEquals("ADD A, -85", instruction.toString())
    }
}
