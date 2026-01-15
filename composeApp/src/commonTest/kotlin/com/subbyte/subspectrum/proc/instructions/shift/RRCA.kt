package com.subbyte.subspectrum.proc.instructions.shift

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RRCATest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = RRCA.decode(0x0FL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x0F.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeRotateRight() {
        Registers.registerSet.setA(0x01.toByte()) // 00000001

        val instruction = RRCA(
            address = 0x1000u,
            bytes = byteArrayOf(0x0F.toByte())
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getA()) // 00000000
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry set to bit 0
    }

    @Test
    fun executeNoCarry() {
        Registers.registerSet.setA(0x02.toByte()) // 00000010

        val instruction = RRCA(
            address = 0x1000u,
            bytes = byteArrayOf(0x0F.toByte())
        )

        instruction.execute()

        assertEquals(0x01.toByte(), Registers.registerSet.getA()) // 00000001
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag()) // Carry 0
    }

    @Test
    fun toStringFormat() {
        val instruction = RRCA(
            address = 0x0000u,
            bytes = byteArrayOf(0x0F.toByte())
        )

        assertEquals("RRCA", instruction.toString())
    }
}
