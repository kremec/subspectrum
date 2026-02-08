package com.subbyte.subspectrum.proc.instructions.control

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.*

class CPLTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = CPL.decode(0x2FL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x2F.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeComplementAllZeros() {
        Registers.registerSet.setA(0x00.toByte())
        Registers.registerSet.setSFlag(true)
        Registers.registerSet.setZFlag(false)
        Registers.registerSet.setPVFlag(true)
        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setNFlag(false)
        Registers.registerSet.setCFlag(true)

        val instruction = CPL(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0x2F.toByte()))
        )

        instruction.execute()

        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getHFlag())
        assertTrue(Registers.registerSet.getNFlag())
        // Other flags unchanged
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getPVFlag())
        assertTrue(Registers.registerSet.getCFlag())
    }

    @Test
    fun executeComplementAllOnes() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setSFlag(false)
        Registers.registerSet.setZFlag(true)
        Registers.registerSet.setPVFlag(false)
        Registers.registerSet.setHFlag(true)
        Registers.registerSet.setNFlag(true)
        Registers.registerSet.setCFlag(false)

        val instruction = CPL(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0x2F.toByte()))
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getHFlag())
        assertTrue(Registers.registerSet.getNFlag())
        // Other flags unchanged
        assertFalse(Registers.registerSet.getSFlag())
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getCFlag())
    }

    @Test
    fun executeComplementMixed() {
        Registers.registerSet.setA(0xAA.toByte()) // 10101010
        Registers.registerSet.setSFlag(true)
        Registers.registerSet.setZFlag(false)
        Registers.registerSet.setPVFlag(true)
        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setNFlag(false)
        Registers.registerSet.setCFlag(true)

        val instruction = CPL(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0x2F.toByte()))
        )

        instruction.execute()

        assertEquals(0x55.toByte(), Registers.registerSet.getA()) // 01010101
        assertTrue(Registers.registerSet.getHFlag())
        assertTrue(Registers.registerSet.getNFlag())
        // Other flags unchanged
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getPVFlag())
        assertTrue(Registers.registerSet.getCFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = CPL(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0x2F.toByte()))
        )

        assertEquals("CPL", instruction.toString())
    }
}