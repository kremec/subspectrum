package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class XORrTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = XORr.decode(0xA9L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0xA9.toByte(), instruction.bytes[0])

        val xorr = instruction as XORr
        assertEquals(RegisterCode.C, xorr.sourceRegister)
    }

    @Test
    fun executeXorRegisters() {
        Registers.registerSet.setA(0x0F.toByte())
        Registers.registerSet.setRegister(RegisterCode.C, 0x0F.toByte())

        val instruction = XORr(
            address = 0x1000u,
            bytes = byteArrayOf(0xA9.toByte()),
            sourceRegister = RegisterCode.C
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = XORr(
            address = 0x0000u,
            bytes = byteArrayOf(0xA9.toByte()),
            sourceRegister = RegisterCode.C
        )

        assertEquals("XOR C", instruction.toString())
    }
}
