package com.subbyte.subspectrum.proc.instructions.bit

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BITbrTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = BITbr.decode(0xCB40L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xCB.toByte(), instruction.bytes[0])
        assertEquals(0x40.toByte(), instruction.bytes[1])

        val bitbr = instruction as BITbr
        assertEquals(0, bitbr.bit)
        assertEquals(RegisterCode.B, bitbr.sourceRegister)
    }

    @Test
    fun executeBitTestBitSet() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x01.toByte()) // bit 0 set

        val instruction = BITbr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x40.toByte()),
            bit = 0,
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getZFlag()) // Z=0 because bit is set
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeBitTestBitClear() {
        Registers.registerSet.setRegister(RegisterCode.B, 0xFE.toByte()) // bit 0 clear

        val instruction = BITbr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x40.toByte()),
            bit = 0,
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getZFlag()) // Z=1 because bit is clear
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeBitTestBit7() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x80.toByte()) // bit 7 set

        val instruction = BITbr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x78.toByte()),
            bit = 7,
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getZFlag()) // Z=0 because bit 7 is set
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = BITbr(
            address = 0x0000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x40.toByte()),
            bit = 0,
            sourceRegister = RegisterCode.B
        )

        assertEquals("BIT 0, B", instruction.toString())
    }
}