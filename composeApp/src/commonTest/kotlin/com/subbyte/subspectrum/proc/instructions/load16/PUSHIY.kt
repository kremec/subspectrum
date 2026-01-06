package com.subbyte.subspectrum.proc.instructions.load16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PUSHIYTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = PUSHIY.decode(0xFDE5L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xFD.toByte(), instruction.bytes[0])
        assertEquals(0xE5.toByte(), instruction.bytes[1])

        val instructionTyped = instruction as PUSHIY
    }

    @Test
    fun executePushIY() {
        Registers.specialPurposeRegisters.setSP(0x1234.toShort())
        Registers.specialPurposeRegisters.setIY(0xABCD.toShort())

        val instruction = PUSHIY(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xE5.toByte())
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Memory.memorySet.getMemoryCell(0x1233u))
        assertEquals(0xCD.toByte(), Memory.memorySet.getMemoryCell(0x1232u))
        assertEquals(0x1232.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun toStringFormat() {
        val instruction = PUSHIY(
            address = 0x0000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xE5.toByte())
        )

        assertEquals("PUSH IY", instruction.toString())
    }
}
