package com.subbyte.subspectrum.proc.instructions.shift

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RLATest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = RLA.decode(0x17L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x17.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeRotateLeftThroughCarry() {
        Registers.registerSet.setA(0x80.toByte()) // 10000000
        Registers.registerSet.setCFlag(false)

        val instruction = RLA(
            address = 0x1000u,
            bytes = byteArrayOf(0x17.toByte())
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getA()) // 00000000
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag()) // Carry was set to bit 7
    }

    @Test
    fun executeWithCarryIn() {
        Registers.registerSet.setA(0x40.toByte()) // 01000000
        Registers.registerSet.setCFlag(true)

        val instruction = RLA(
            address = 0x1000u,
            bytes = byteArrayOf(0x17.toByte())
        )

        instruction.execute()

        assertEquals(0x81.toByte(), Registers.registerSet.getA()) // 10000001
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getCFlag()) // Carry was 0
    }

    @Test
    fun toStringFormat() {
        val instruction = RLA(
            address = 0x0000u,
            bytes = byteArrayOf(0x17.toByte())
        )

        assertEquals("RLA", instruction.toString())
    }
}
