package com.subbyte.subspectrum.proc.instructions.ex

import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class EXAFAFTest {
    @BeforeTest
    fun setup() {
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = EXAFAF.decode(0x08L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x08.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeSwapAFWithShadowAF() {
        Registers.normalRegisterSet.setAF(0x1234.toShort())
        Registers.shadowRegisterSet.setAF(0xABCD.toShort())

        val instruction = EXAFAF(
            address = 0x1000u,
            bytes = byteArrayOf(0x08)
        )

        instruction.execute()

        assertEquals(0xABCD.toShort(), Registers.normalRegisterSet.getAF())
        assertEquals(0x1234.toShort(), Registers.shadowRegisterSet.getAF())
    }

    @Test
    fun toStringFormat() {
        val instruction = EXAFAF(
            address = 0x0000u,
            bytes = byteArrayOf(0x08)
        )

        assertEquals("EX AF, AF'", instruction.toString())
    }
}
