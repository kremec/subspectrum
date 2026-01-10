package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class INCrTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = INCr.decode(0x04L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x04.toByte(), instruction.bytes[0])

        val incr = instruction as INCr
        assertEquals(RegisterCode.B, incr.sourceRegister)
    }

    @Test
    fun executeIncrementRegister() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x0F.toByte())

        val instruction = INCr(
            address = 0x1000u,
            bytes = byteArrayOf(0x04.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x10.toByte(), Registers.registerSet.getRegister(RegisterCode.B))
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeIncrementToZero() {
        Registers.registerSet.setRegister(RegisterCode.B, 0xFF.toByte())

        val instruction = INCr(
            address = 0x1000u,
            bytes = byteArrayOf(0x04.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getRegister(RegisterCode.B))
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = INCr(
            address = 0x0000u,
            bytes = byteArrayOf(0x04.toByte()),
            sourceRegister = RegisterCode.B
        )

        assertEquals("INC B", instruction.toString())
    }
}
