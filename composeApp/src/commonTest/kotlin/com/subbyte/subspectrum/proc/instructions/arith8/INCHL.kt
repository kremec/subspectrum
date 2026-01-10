package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class INCHLTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = INCHL.decode(0x34L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x34.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeIncrementMemoryHL() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x0F.toByte())

        val instruction = INCHL(
            address = 0x1000u,
            bytes = byteArrayOf(0x34.toByte())
        )

        instruction.execute()

        assertEquals(0x10.toByte(), Memory.memorySet.getMemoryCell(0x2000u))
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = INCHL(
            address = 0x0000u,
            bytes = byteArrayOf(0x34.toByte())
        )

        assertEquals("INC (HL)", instruction.toString())
    }
}
