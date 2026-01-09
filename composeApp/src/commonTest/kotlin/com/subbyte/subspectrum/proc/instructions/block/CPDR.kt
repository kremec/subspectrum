package com.subbyte.subspectrum.proc.instructions.block

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CPDRTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = CPDR.decode(0xEDB9L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0xB9.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeCompareDecrementRepeatNotMatch() {
        Registers.specialPurposeRegisters.setPC(0x1000.toShort())
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setHL(0x2000)
        Memory.memorySet.setMemoryCell(0x2000u, 0x05)
        Registers.registerSet.setBC(0x0003)

        val instruction = CPDR(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0xB9.toByte())
        )

        instruction.execute()

        assertEquals(0x0FFE, Registers.specialPurposeRegisters.getPC())
        assertEquals(0x1FFF, Registers.registerSet.getHL())
        assertEquals(0x0002, Registers.registerSet.getBC())
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getPVFlag())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeCompareDecrementRepeatMatch() {
        Registers.specialPurposeRegisters.setPC(0x1000.toShort())
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setHL(0x2000)
        Memory.memorySet.setMemoryCell(0x2000u, 0x10)
        Registers.registerSet.setBC(0x0003)

        val instruction = CPDR(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0xB9.toByte())
        )

        instruction.execute()

        assertEquals(0x0FFE, Registers.specialPurposeRegisters.getPC())
        assertEquals(0x1FFF, Registers.registerSet.getHL())
        assertEquals(0x0002, Registers.registerSet.getBC())
        assertFalse(Registers.registerSet.getSFlag())
        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getPVFlag())
    }

    @Test
    fun executeCompareDecrementRepeatBCZero() {
        Registers.specialPurposeRegisters.setPC(0x1000.toShort())
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setHL(0x2000)
        Memory.memorySet.setMemoryCell(0x2000u, 0x05)
        Registers.registerSet.setBC(0x0001)

        val instruction = CPDR(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0xB9.toByte())
        )

        instruction.execute()

        assertEquals(0x0FFE, Registers.specialPurposeRegisters.getPC())
        assertEquals(0x1FFF, Registers.registerSet.getHL())
        assertEquals(0x0000, Registers.registerSet.getBC())
        assertFalse(Registers.registerSet.getPVFlag())
    }

    @Test
    fun executeCompareDecrementRepeatBothConditionsFalse() {
        Registers.specialPurposeRegisters.setPC(0x1000.toShort())
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setHL(0x2000)
        Memory.memorySet.setMemoryCell(0x2000u, 0x10)
        Registers.registerSet.setBC(0x0001)

        val instruction = CPDR(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0xB9.toByte())
        )

        instruction.execute()

        assertEquals(0x0FFE, Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun toStringFormat() {
        val instruction = CPDR(
            address = 0x0000u,
            bytes = byteArrayOf(0xED.toByte(), 0xB9.toByte())
        )

        assertEquals("CPDR", instruction.toString())
    }
}
