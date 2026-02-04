package com.subbyte.subspectrum.proc.instructions.control

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.Processor
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HALTTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
        Processor.inHalt = false
    }

    @Test
    fun decodeInstruction() {
        val instruction = HALT.decode(0x76L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x76.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeSetsHaltAndDecrementsPC() {
        Registers.specialPurposeRegisters.setPC(0x1234)
        Processor.inHalt = false

        val instruction = HALT(
            address = 0x1000u,
            bytes = byteArrayOf(0x76.toByte())
        )

        instruction.execute()

        assertTrue(Processor.inHalt)
        // PC should be decremented by instruction size (1 byte) to point to HALT again
        assertEquals(0x1233, Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeAtAddressBoundary() {
        Registers.specialPurposeRegisters.setPC(0x0001)
        Processor.inHalt = false

        val instruction = HALT(
            address = 0x0000u,
            bytes = byteArrayOf(0x76.toByte())
        )

        instruction.execute()

        assertTrue(Processor.inHalt)
        assertEquals(0x0000, Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executePreservesRegisterState() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setA(0x55.toByte())
        Registers.registerSet.setB(0xAA.toByte())
        Registers.registerSet.setCFlag(true)
        Registers.registerSet.setZFlag(false)
        Processor.inHalt = false

        val instruction = HALT(
            address = 0x1000u,
            bytes = byteArrayOf(0x76.toByte())
        )

        instruction.execute()

        // Verify other registers are unchanged
        assertEquals(0x55.toByte(), Registers.registerSet.getA())
        assertEquals(0xAA.toByte(), Registers.registerSet.getB())
        assertTrue(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertTrue(Processor.inHalt)
    }

    @Test
    fun toStringFormat() {
        val instruction = HALT(
            address = 0x0000u,
            bytes = byteArrayOf(0x76.toByte())
        )

        assertEquals("HALT", instruction.toString())
    }
}
