package com.subbyte.subspectrum.proc.instructions.undocumented.cb

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.*

class SLLrTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = SLLr.decode(0xCB30L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xCB.toByte(), instruction.bytes[0])
        assertEquals(0x30.toByte(), instruction.bytes[1])

        val sllr = instruction as SLLr
        assertEquals(RegisterCode.B, sllr.sourceRegister)
    }

    @Test
    fun executeShiftLeftLogicalOneWithCarry() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x80.toByte())

        val instruction = SLLr(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xCB.toByte(), 0x30.toByte())),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x01.toByte(), Registers.registerSet.getRegister(RegisterCode.B))
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag())
    }

    @Test
    fun executeShiftLeftLogicalOneWithoutCarry() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x40.toByte())

        val instruction = SLLr(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xCB.toByte(), 0x30.toByte())),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x81.toByte(), Registers.registerSet.getRegister(RegisterCode.B))
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertTrue(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun executeShiftLeftLogicalOneFromZero() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x00.toByte())

        val instruction = SLLr(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xCB.toByte(), 0x30.toByte())),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x01.toByte(), Registers.registerSet.getRegister(RegisterCode.B))
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun testParityEven() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x01.toByte())

        val instruction = SLLr(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xCB.toByte(), 0x30.toByte())),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getPVFlag())
        assertEquals(0x03.toByte(), Registers.registerSet.getRegister(RegisterCode.B))
    }

    @Test
    fun testParityOdd() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x09.toByte())

        val instruction = SLLr(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xCB.toByte(), 0x30.toByte())),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getPVFlag())
        assertEquals(0x13.toByte(), Registers.registerSet.getRegister(RegisterCode.B))
    }

    @Test
    fun toStringFormat() {
        val instruction = SLLr(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xCB.toByte(), 0x30.toByte())),
            sourceRegister = RegisterCode.B
        )

        assertEquals("SLL B", instruction.toString())
    }
}
