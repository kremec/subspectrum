package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SUBAnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = SUBAn.decode(0xD6ABL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xD6.toByte(), instruction.bytes[0])
        assertEquals(0xAB.toByte(), instruction.bytes[1])

        val subAn = instruction as SUBAn
        assertEquals(0xAB.toByte(), subAn.sourceByte)
    }

    @Test
    fun executeSubImmediate() {
        Registers.registerSet.setA(0x30.toByte())

        val instruction = SUBAn(
            address = 0x1000u,
            bytes = byteArrayOf(0xD6.toByte(), 0x10.toByte()),
            sourceByte = 0x10.toByte()
        )

        instruction.execute()

        assertEquals(0x20.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = SUBAn(
            address = 0x0000u,
            bytes = byteArrayOf(0xD6.toByte(), 0xAB.toByte()),
            sourceByte = 0xAB.toByte()
        )

        assertEquals("SUB A, -85", instruction.toString())
    }
}
