package com.subbyte.subspectrum.proc.instructions.shift

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RLCrTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = RLCr.decode(0xCB00L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xCB.toByte(), instruction.bytes[0])
        assertEquals(0x00.toByte(), instruction.bytes[1])

        val rlcr = instruction as RLCr
        assertEquals(RegisterCode.B, rlcr.sourceRegister)
    }

    @Test
    fun executeRotateLeft() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x80.toByte()) // 10000000

        val instruction = RLCr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x00.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x01.toByte(), Registers.registerSet.getRegister(RegisterCode.B)) // 00000001
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry set to bit 7
    }

    @Test
    fun executeRotateToZero() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x00.toByte())

        val instruction = RLCr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x00.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getRegister(RegisterCode.B))
        assertFalse(Registers.registerSet.getSFlag())
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = RLCr(
            address = 0x0000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x00.toByte()),
            sourceRegister = RegisterCode.B
        )

        assertEquals("RLC B", instruction.toString())
    }
}
