package com.subbyte.subspectrum.proc.instructions.block

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LDITest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDI.decode(0xEDA0L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0xA0.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeLoadAndIncrement() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0xAB.toByte())
        Registers.registerSet.setDE(0x3000.toShort())
        Memory.memorySet.setMemoryCell(0x3000u, 0x00.toByte())
        Registers.registerSet.setBC(0x0003.toShort())

        val instruction = LDI(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0xA0.toByte())
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Memory.memorySet.getMemoryCell(0x3000u))
        assertEquals(0x2001.toShort(), Registers.registerSet.getHL())
        assertEquals(0x3001.toShort(), Registers.registerSet.getDE())
        assertEquals(0x0002.toShort(), Registers.registerSet.getBC())
        assertFalse(Registers.registerSet.getHFlag())
        assertTrue(Registers.registerSet.getPVFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executePVFlagWhenBCReachesZero() {
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0xAB.toByte())
        Registers.registerSet.setDE(0x3000.toShort())
        Registers.registerSet.setBC(0x0001.toShort())

        val instruction = LDI(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0xA0.toByte())
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getPVFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = LDI(
            address = 0x0000u,
            bytes = byteArrayOf(0xED.toByte(), 0xA0.toByte())
        )

        assertEquals("LDI", instruction.toString())
    }
}
