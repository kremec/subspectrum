package com.subbyte.subspectrum.proc.instructions.shift

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RLCATest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = RLCA.decode(0x07L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x07.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeRotateLeft() {
        Registers.registerSet.setA(0x80.toByte()) // 10000000

        val instruction = RLCA(
            address = 0x1000u,
            bytes = byteArrayOf(0x07.toByte())
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getA()) // 00000000
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry set to bit 7
    }

    @Test
    fun executeNoCarry() {
        Registers.registerSet.setA(0x40.toByte()) // 01000000

        val instruction = RLCA(
            address = 0x1000u,
            bytes = byteArrayOf(0x07.toByte())
        )

        instruction.execute()

        assertEquals(0x80.toByte(), Registers.registerSet.getA()) // 10000000
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag()) // Carry 0
    }

    @Test
    fun toStringFormat() {
        val instruction = RLCA(
            address = 0x0000u,
            bytes = byteArrayOf(0x07.toByte())
        )

        assertEquals("RLCA", instruction.toString())
    }
}
