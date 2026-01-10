package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ORrTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ORr.decode(0xB1L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0xB1.toByte(), instruction.bytes[0])

        val orr = instruction as ORr
        assertEquals(RegisterCode.C, orr.sourceRegister)
    }

    @Test
    fun executeOrRegisters() {
        Registers.registerSet.setA(0x0F.toByte())
        Registers.registerSet.setRegister(RegisterCode.C, 0xF0.toByte())

        val instruction = ORr(
            address = 0x1000u,
            bytes = byteArrayOf(0xB1.toByte()),
            sourceRegister = RegisterCode.C
        )

        instruction.execute()

        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = ORr(
            address = 0x0000u,
            bytes = byteArrayOf(0xB1.toByte()),
            sourceRegister = RegisterCode.C
        )

        assertEquals("OR C", instruction.toString())
    }
}
