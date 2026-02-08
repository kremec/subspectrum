package com.subbyte.subspectrum.proc.instructions.control

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.*

class SCFTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = SCF.decode(0x37L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x37.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeSetsCarryFlag() {
        Registers.registerSet.setCFlag(false)
        Registers.registerSet.setHFlag(true)
        Registers.registerSet.setNFlag(true)
        Registers.registerSet.setA(0x55.toByte())
        Registers.registerSet.setSFlag(true)
        Registers.registerSet.setZFlag(false)
        Registers.registerSet.setPVFlag(true)

        val instruction = SCF(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0x37.toByte()))
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
        // Other flags and registers unchanged
        assertEquals(0x55.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getPVFlag())
    }

    @Test
    fun executeCarryFlagAlreadySet() {
        Registers.registerSet.setCFlag(true)
        Registers.registerSet.setHFlag(true)
        Registers.registerSet.setNFlag(true)
        Registers.registerSet.setA(0xAA.toByte())
        Registers.registerSet.setSFlag(false)
        Registers.registerSet.setZFlag(true)
        Registers.registerSet.setPVFlag(false)

        val instruction = SCF(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0x37.toByte()))
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
        // Other flags and registers unchanged
        assertEquals(0xAA.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getSFlag())
        assertTrue(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getPVFlag())
    }

    @Test
    fun executeResetsHAndNRegardlessOfInitialState() {
        // Test with H and N already clear
        Registers.registerSet.setCFlag(false)
        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setNFlag(false)

        val instruction = SCF(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0x37.toByte()))
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = SCF(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0x37.toByte()))
        )

        assertEquals("SCF", instruction.toString())
    }
}
