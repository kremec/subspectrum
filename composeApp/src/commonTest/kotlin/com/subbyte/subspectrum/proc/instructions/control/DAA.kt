package com.subbyte.subspectrum.proc.instructions.control

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.*

class DAATest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = DAA.decode(0x27L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x27.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeNoCorrection() {
        Registers.registerSet.setA(0x12.toByte())
        Registers.registerSet.setNFlag(false) // Addition
        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setCFlag(false)
        Registers.registerSet.setSFlag(true)
        Registers.registerSet.setZFlag(false)
        Registers.registerSet.setPVFlag(true)

        val instruction = DAA(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0x27.toByte()))
        )

        instruction.execute()

        assertEquals(0x12.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getCFlag()) // No carry
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
        // S, Z, PV may change based on parity
        assertFalse(Registers.registerSet.getZFlag()) // 0x12 != 0
    }

    @Test
    fun executeLowerNibbleCorrectionAddition() {
        Registers.registerSet.setA(0x1A.toByte()) // 1A > 19 in BCD, needs correction
        Registers.registerSet.setNFlag(false) // Addition
        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setCFlag(false)

        val instruction = DAA(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0x27.toByte()))
        )

        instruction.execute()

        assertEquals(0x20.toByte(), Registers.registerSet.getA()) // 1A + 06 = 20
        assertFalse(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeUpperNibbleCorrectionAddition() {
        Registers.registerSet.setA(0x9A.toByte()) // > 99, needs correction
        Registers.registerSet.setNFlag(false) // Addition
        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setCFlag(false)

        val instruction = DAA(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0x27.toByte()))
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getA()) // 9A + 66 = 100, but wraps to 00
        assertTrue(Registers.registerSet.getCFlag()) // Carry set
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeBothCorrectionsAddition() {
        Registers.registerSet.setA(0x9F.toByte()) // Both nibbles need correction
        Registers.registerSet.setNFlag(false) // Addition
        Registers.registerSet.setHFlag(true)
        Registers.registerSet.setCFlag(true)

        val instruction = DAA(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0x27.toByte()))
        )

        instruction.execute()

        assertEquals(0x05.toByte(), Registers.registerSet.getA()) // 9F + 66 = 105, wraps to 05
        assertTrue(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeLowerNibbleCorrectionSubtraction() {
        Registers.registerSet.setA(0x02.toByte())
        Registers.registerSet.setNFlag(true) // Subtraction
        Registers.registerSet.setHFlag(true)
        Registers.registerSet.setCFlag(false)

        val instruction = DAA(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0x27.toByte()))
        )

        instruction.execute()

        assertEquals(0xFC.toByte(), Registers.registerSet.getA()) // 02 - 06 = FC
        assertFalse(Registers.registerSet.getCFlag()) // No upper adjustment
        assertTrue(Registers.registerSet.getHFlag())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeUpperNibbleCorrectionSubtraction() {
        Registers.registerSet.setA(0x02.toByte())
        Registers.registerSet.setNFlag(true) // Subtraction
        Registers.registerSet.setHFlag(false)
        Registers.registerSet.setCFlag(true)

        val instruction = DAA(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0x27.toByte()))
        )

        instruction.execute()

        assertEquals(0xA2.toByte(), Registers.registerSet.getA()) // 02 - 60 = A2
        assertTrue(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = DAA(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0x27.toByte()))
        )

        assertEquals("DAA", instruction.toString())
    }
}
