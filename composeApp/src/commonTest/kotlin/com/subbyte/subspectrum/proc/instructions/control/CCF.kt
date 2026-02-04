package com.subbyte.subspectrum.proc.instructions.control

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CCFTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = CCF.decode(0x3FL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x3F.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeComplementCarryFromZero() {
        Registers.registerSet.setCFlag(false)
        Registers.registerSet.setA(0x55.toByte()) // Some value
        Registers.registerSet.setSFlag(true) // Set some flags
        Registers.registerSet.setZFlag(false)
        Registers.registerSet.setPVFlag(true)
        Registers.registerSet.setHFlag(true)
        Registers.registerSet.setNFlag(true)

        val instruction = CCF(
            address = 0x1000u,
            bytes = byteArrayOf(0x3F.toByte())
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getCFlag()) // Complemented from false
        assertFalse(Registers.registerSet.getHFlag()) // Set to old C (false)
        assertFalse(Registers.registerSet.getNFlag()) // Reset
        // Other flags unchanged
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getPVFlag())
        assertEquals(0x55.toByte(), Registers.registerSet.getA()) // A unchanged
    }

    @Test
    fun executeComplementCarryFromOne() {
        Registers.registerSet.setCFlag(true)
        Registers.registerSet.setA(0xAA.toByte())
        Registers.registerSet.setSFlag(false)
        Registers.registerSet.setZFlag(true)
        Registers.registerSet.setPVFlag(false)
        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setNFlag(true)

        val instruction = CCF(
            address = 0x1000u,
            bytes = byteArrayOf(0x3F.toByte())
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getCFlag()) // Complemented from true
        assertTrue(Registers.registerSet.getHFlag()) // Set to old C (true)
        assertFalse(Registers.registerSet.getNFlag()) // Reset
        // Other flags unchanged
        assertFalse(Registers.registerSet.getSFlag())
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertEquals(0xAA.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun toStringFormat() {
        val instruction = CCF(
            address = 0x0000u,
            bytes = byteArrayOf(0x3F.toByte())
        )

        assertEquals("CCF", instruction.toString())
    }
}