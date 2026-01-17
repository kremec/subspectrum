package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class INCIXdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = INCIXd.decode(0xDD3405L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0x34.toByte(), instruction.bytes[1])
        assertEquals(0x05.toByte(), instruction.bytes[2])

        val incIXd = instruction as INCIXd
        assertEquals(0x05.toByte(), incIXd.displacement)
    }

    @Test
    fun executeIncrementWithIXOffset() {
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x0F.toByte())

        val instruction = INCIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x34.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertEquals(0x10.toByte(), Memory.memorySet.getMemoryCell(0x2005u))
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun testSignFlag() {
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x7F.toByte())

        val instruction = INCIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x34.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getSFlag())
        assertEquals(0x80.toByte(), Memory.memorySet.getMemoryCell(0x2005u))
    }

    @Test
    fun testHalfCarryFlag() {
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x0F.toByte())

        val instruction = INCIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x34.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getHFlag())
        assertEquals(0x10.toByte(), Memory.memorySet.getMemoryCell(0x2005u))
    }

    @Test
    fun testOverflowFlag() {
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x7F.toByte())

        val instruction = INCIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x34.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getPVFlag())
        assertEquals(0x80.toByte(), Memory.memorySet.getMemoryCell(0x2005u))
    }

    @Test
    fun testNoOverflow() {
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x40.toByte())

        val instruction = INCIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x34.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getPVFlag())
        assertEquals(0x41.toByte(), Memory.memorySet.getMemoryCell(0x2005u))
    }

    @Test
    fun testNFlagAlwaysReset() {
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x10.toByte())
        Registers.registerSet.setNFlag(true)

        val instruction = INCIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x34.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getNFlag())
        assertEquals(0x11.toByte(), Memory.memorySet.getMemoryCell(0x2005u))
    }

    @Test
    fun testCFlagNotAffected() {
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x10.toByte())
        Registers.registerSet.setCFlag(true)

        val instruction = INCIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x34.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getCFlag()) // Should remain set
        assertEquals(0x11.toByte(), Memory.memorySet.getMemoryCell(0x2005u))
    }

    @Test
    fun toStringFormat() {
        val instruction = INCIXd(
            address = 0x0000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x34.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        assertEquals("INC (IX + 5)", instruction.toString())
    }
}
