package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class INCIYdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = INCIYd.decode(0xFD3405L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0xFD.toByte(), instruction.bytes[0])
        assertEquals(0x34.toByte(), instruction.bytes[1])
        assertEquals(0x05.toByte(), instruction.bytes[2])

        val incIYd = instruction as INCIYd
        assertEquals(0x05.toByte(), incIYd.displacement)
    }

    @Test
    fun executeIncrementWithIYOffset() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x0F.toByte())

        val instruction = INCIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x34.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertEquals(0x10.toByte(), Memory.memorySet.getMemoryCell(0x2005u))
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = INCIYd(
            address = 0x0000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x34.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        assertEquals("INC (IY + 5)", instruction.toString())
    }
}
