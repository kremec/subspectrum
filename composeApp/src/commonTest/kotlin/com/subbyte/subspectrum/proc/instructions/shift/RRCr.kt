package com.subbyte.subspectrum.proc.instructions.shift

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RRCrTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = RRCr.decode(0xCB08L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xCB.toByte(), instruction.bytes[0])
        assertEquals(0x08.toByte(), instruction.bytes[1])

        val rrcr = instruction as RRCr
        assertEquals(RegisterCode.B, rrcr.sourceRegister)
    }

    @Test
    fun executeRotateRightWithCarry() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x01.toByte()) // 00000001

        val instruction = RRCr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x08.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x80.toByte(), Registers.registerSet.getRegister(RegisterCode.B)) // 10000000 (bit 0 becomes bit 7)
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry set to bit 0
    }

    @Test
    fun executeRotateRightWithCarryFalse() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x02.toByte()) // 00000010

        val instruction = RRCr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x08.toByte()),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x01.toByte(), Registers.registerSet.getRegister(RegisterCode.B)) // 00000001 (shifted right, bit 0 becomes bit 7)
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag()) // Carry not set because bit 0 was 0
    }

    @Test
    fun executeRotateToZero() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x00.toByte())

        val instruction = RRCr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x08.toByte()),
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
        val instruction = RRCr(
            address = 0x0000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x08.toByte()),
            sourceRegister = RegisterCode.B
        )

        assertEquals("RRC B", instruction.toString())
    }
}
