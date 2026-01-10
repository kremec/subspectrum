package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ANDIXdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ANDIXd.decode(0xDDA605L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0xA6.toByte(), instruction.bytes[1])
        assertEquals(0x05.toByte(), instruction.bytes[2])

        val andIXd = instruction as ANDIXd
        assertEquals(0x05.toByte(), andIXd.displacement)
    }

    @Test
    fun executeAndWithIXOffset() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x0F.toByte())

        val instruction = ANDIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xA6.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertEquals(0x0F.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = ANDIXd(
            address = 0x0000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xA6.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        assertEquals("AND (IX + 5)", instruction.toString())
    }
}
