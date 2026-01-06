package com.subbyte.subspectrum.proc.instructions.load16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PUSHIXTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = PUSHIX.decode(0xDDE5L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0xE5.toByte(), instruction.bytes[1])

        val instructionTyped = instruction as PUSHIX
    }

    @Test
    fun executePushIX() {
        Registers.specialPurposeRegisters.setSP(0x1234.toShort())
        Registers.specialPurposeRegisters.setIX(0xABCD.toShort())

        val instruction = PUSHIX(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xE5.toByte())
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Memory.memorySet.getMemoryCell(0x1233u))
        assertEquals(0xCD.toByte(), Memory.memorySet.getMemoryCell(0x1232u))
        assertEquals(0x1232.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun toStringFormat() {
        val instruction = PUSHIX(
            address = 0x0000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xE5.toByte())
        )

        assertEquals("PUSH IX", instruction.toString())
    }
}
