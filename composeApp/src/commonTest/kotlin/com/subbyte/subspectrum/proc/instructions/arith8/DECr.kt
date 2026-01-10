package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DECrTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = DECr.decode(0x05L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x05.toByte(), instruction.bytes[0])

        val decr = instruction as DECr
        assertEquals(RegisterCode.B, decr.sourceRegister)
    }

    @Test
    fun executeDecrementRegister() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x10.toByte())

        val instruction = DECr(
            address = 0x1000u,
            bytes = byteArrayOf(0x05.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x0F.toByte(), Registers.registerSet.getRegister(RegisterCode.B))
        assertFalse(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeDecrementToZero() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x01.toByte())

        val instruction = DECr(
            address = 0x1000u,
            bytes = byteArrayOf(0x05.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getRegister(RegisterCode.B))
        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = DECr(
            address = 0x0000u,
            bytes = byteArrayOf(0x05.toByte()),
            sourceRegister = RegisterCode.B
        )

        assertEquals("DEC B", instruction.toString())
    }
}
