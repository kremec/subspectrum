package com.subbyte.subspectrum.proc.instructions.ex

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class EXSPIXTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = EXSPIX.decode(0xDDE3L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0xE3.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeSwapIXWithSP() {
        Registers.specialPurposeRegisters.setIX(0x1234.toShort())
        Registers.specialPurposeRegisters.setSP(0xFFFE.toShort())
        Memory.memorySet.setMemoryCell(0xFFFEu, 0xAB.toByte())
        Memory.memorySet.setMemoryCell(0xFFFFu, 0xCD.toByte())

        val instruction = EXSPIX(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xE3.toByte())
        )

        instruction.execute()

        assertEquals(0xCDAB.toShort(), Registers.specialPurposeRegisters.getIX())
        assertEquals(0x34.toByte(), Memory.memorySet.getMemoryCell(0xFFFEu))
        assertEquals(0x12.toByte(), Memory.memorySet.getMemoryCell(0xFFFFu))
    }

    @Test
    fun toStringFormat() {
        val instruction = EXSPIX(
            address = 0x0000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xE3.toByte())
        )

        assertEquals("EX (SP), IX", instruction.toString())
    }
}
