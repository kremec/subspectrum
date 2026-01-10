package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DECHLTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = DECHL.decode(0x35L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x35.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeDecrementMemoryHL() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x10.toByte())

        val instruction = DECHL(
            address = 0x1000u,
            bytes = byteArrayOf(0x35.toByte())
        )

        instruction.execute()

        assertEquals(0x0F.toByte(), Memory.memorySet.getMemoryCell(0x2000u))
        assertFalse(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = DECHL(
            address = 0x0000u,
            bytes = byteArrayOf(0x35.toByte())
        )

        assertEquals("DEC (HL)", instruction.toString())
    }
}
