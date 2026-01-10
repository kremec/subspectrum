package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ORIYdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ORIYd.decode(0xFDB605L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0xFD.toByte(), instruction.bytes[0])
        assertEquals(0xB6.toByte(), instruction.bytes[1])
        assertEquals(0x05.toByte(), instruction.bytes[2])

        val orIYd = instruction as ORIYd
        assertEquals(0x05.toByte(), orIYd.displacement)
    }

    @Test
    fun executeOrWithIYOffset() {
        Registers.registerSet.setA(0x0F.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0xF0.toByte())

        val instruction = ORIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xB6.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = ORIYd(
            address = 0x0000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xB6.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        assertEquals("OR (IY + 5)", instruction.toString())
    }
}
