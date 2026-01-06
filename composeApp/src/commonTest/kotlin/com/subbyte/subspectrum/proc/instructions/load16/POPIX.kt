package com.subbyte.subspectrum.proc.instructions.load16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class POPIXTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = POPIX.decode(0xDDE1L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0xE1.toByte(), instruction.bytes[1])

        val instructionTyped = instruction as POPIX
    }

    @Test
    fun executePopToIX() {
        Registers.specialPurposeRegisters.setSP(0x1234.toShort())
        Memory.memorySet.setMemoryCell(0x1234u, 0xCD.toByte())
        Memory.memorySet.setMemoryCell(0x1235u, 0xAB.toByte())

        val instruction = POPIX(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xE1.toByte())
        )

        instruction.execute()

        assertEquals(0xABCD.toShort(), Registers.specialPurposeRegisters.getIX())
        assertEquals(0x1236.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun toStringFormat() {
        val instruction = POPIX(
            address = 0x0000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xE1.toByte())
        )

        assertEquals("PUSH IX", instruction.toString())
    }
}
