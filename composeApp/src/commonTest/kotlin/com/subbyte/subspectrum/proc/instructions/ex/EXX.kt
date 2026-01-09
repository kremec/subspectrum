package com.subbyte.subspectrum.proc.instructions.ex

import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class EXXTest {
    @BeforeTest
    fun setup() {
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = EXX.decode(0xD9L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0xD9.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeSwapRegisterPairsWithShadow() {
        Registers.normalRegisterSet.setBC(0x1111.toShort())
        Registers.shadowRegisterSet.setBC(0x2222.toShort())
        Registers.normalRegisterSet.setDE(0x3333.toShort())
        Registers.shadowRegisterSet.setDE(0x4444.toShort())
        Registers.normalRegisterSet.setHL(0x5555.toShort())
        Registers.shadowRegisterSet.setHL(0x6666.toShort())

        val instruction = EXX(
            address = 0x1000u,
            bytes = byteArrayOf(0xD9.toByte())
        )

        instruction.execute()

        assertEquals(0x2222.toShort(), Registers.normalRegisterSet.getBC())
        assertEquals(0x1111.toShort(), Registers.shadowRegisterSet.getBC())
        assertEquals(0x4444.toShort(), Registers.normalRegisterSet.getDE())
        assertEquals(0x3333.toShort(), Registers.shadowRegisterSet.getDE())
        assertEquals(0x6666.toShort(), Registers.normalRegisterSet.getHL())
        assertEquals(0x5555.toShort(), Registers.shadowRegisterSet.getHL())
    }

    @Test
    fun toStringFormat() {
        val instruction = EXX(
            address = 0x0000u,
            bytes = byteArrayOf(0xD9.toByte())
        )

        assertEquals("EXX'", instruction.toString())
    }
}
