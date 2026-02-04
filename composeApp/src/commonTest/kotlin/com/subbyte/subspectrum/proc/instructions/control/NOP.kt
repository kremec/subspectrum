package com.subbyte.subspectrum.proc.instructions.control

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NOPTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = NOP.decode(0x00L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x00.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeDoesNothing() {
        // Set up some initial state
        Registers.registerSet.setA(0x55.toByte())
        Registers.registerSet.setB(0xAA.toByte())
        Registers.registerSet.setC(0x33.toByte())
        Registers.registerSet.setD(0xCC.toByte())
        Registers.registerSet.setE(0x77.toByte())
        Registers.registerSet.setH(0x11.toByte())
        Registers.registerSet.setL(0x22.toByte())
        Registers.registerSet.setSFlag(true)
        Registers.registerSet.setZFlag(false)
        Registers.registerSet.setHFlag(true)
        Registers.registerSet.setPVFlag(false)
        Registers.registerSet.setNFlag(true)
        Registers.registerSet.setCFlag(true)

        // Set some memory values
        Memory.memorySet.setMemoryCell(0x0000u, 0xFF.toByte())
        Memory.memorySet.setMemoryCell(0x1000u, 0xEE.toByte())

        val instruction = NOP(
            address = 0x1000u,
            bytes = byteArrayOf(0x00.toByte())
        )

        instruction.execute()

        // Verify nothing changed
        assertEquals(0x55.toByte(), Registers.registerSet.getA())
        assertEquals(0xAA.toByte(), Registers.registerSet.getB())
        assertEquals(0x33.toByte(), Registers.registerSet.getC())
        assertEquals(0xCC.toByte(), Registers.registerSet.getD())
        assertEquals(0x77.toByte(), Registers.registerSet.getE())
        assertEquals(0x11.toByte(), Registers.registerSet.getH())
        assertEquals(0x22.toByte(), Registers.registerSet.getL())
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertTrue(Registers.registerSet.getNFlag())
        assertTrue(Registers.registerSet.getCFlag())

        // Memory unchanged
        assertEquals(0xFF.toByte(), Memory.memorySet.getMemoryCell(0x0000u))
        assertEquals(0xEE.toByte(), Memory.memorySet.getMemoryCell(0x1000u))
    }

    @Test
    fun toStringFormat() {
        val instruction = NOP(
            address = 0x0000u,
            bytes = byteArrayOf(0x00.toByte())
        )

        assertEquals("NOP", instruction.toString())
    }
}