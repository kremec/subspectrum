package com.subbyte.subspectrum.proc.instructions.ex

import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class EXDEHLTest {
    @BeforeTest
    fun setup() {
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = EXDEHL.decode(0xEBL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0xEB.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeSwapDEWithHL() {
        Registers.registerSet.setDE(0x1234.toShort())
        Registers.registerSet.setHL(0xABCD.toShort())

        val instruction = EXDEHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xEB.toByte())
        )

        instruction.execute()

        assertEquals(0xABCD.toShort(), Registers.registerSet.getDE())
        assertEquals(0x1234.toShort(), Registers.registerSet.getHL())
    }

    @Test
    fun toStringFormat() {
        val instruction = EXDEHL(
            address = 0x0000u,
            bytes = byteArrayOf(0xEB.toByte())
        )

        assertEquals("EX DE, HL", instruction.toString())
    }
}
