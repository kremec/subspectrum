package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CPnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = CPn.decode(0xFEABL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xFE.toByte(), instruction.bytes[0])
        assertEquals(0xAB.toByte(), instruction.bytes[1])

        val cpn = instruction as CPn
        assertEquals(0xAB.toByte(), cpn.sourceByte)
    }

    @Test
    fun executeCompareImmediateEqual() {
        Registers.registerSet.setA(0x10.toByte())

        val instruction = CPn(
            address = 0x1000u,
            bytes = byteArrayOf(0xFE.toByte(), 0x10.toByte()),
            sourceByte = 0x10.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertEquals(0x10.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun toStringFormat() {
        val instruction = CPn(
            address = 0x0000u,
            bytes = byteArrayOf(0xFE.toByte(), 0xAB.toByte()),
            sourceByte = 0xAB.toByte()
        )

        assertEquals("CP -85", instruction.toString())
    }
}
