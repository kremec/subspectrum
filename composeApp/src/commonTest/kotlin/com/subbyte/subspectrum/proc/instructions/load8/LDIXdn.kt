package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDIXdnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDIXdn.decode(0xDD3612ABL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(4, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0x36.toByte(), instruction.bytes[1])
        assertEquals(0x12.toByte(), instruction.bytes[2])
        assertEquals(0xAB.toByte(), instruction.bytes[3])

        val ldIXdn = instruction as LDIXdn
        assertEquals(0x12.toByte(), ldIXdn.displacement)
        assertEquals(0xAB.toByte(), ldIXdn.sourceByte)
    }

    @Test
    fun executeLoadImmediateToMemoryIX() {
        Registers.specialPurposeRegisters.setIX(0x2000)

        val instruction = LDIXdn(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x36.toByte(), 0x05.toByte(), 0xFF.toByte()),
            displacement = 0x05.toByte(),
            sourceByte = 0xFF.toByte()
        )

        instruction.execute()

        assertEquals(0xFF.toByte(), Memory.memorySet.getMemoryCell(0x2005u))
    }

    @Test
    fun toStringFormat() {
        val instruction = LDIXdn(
            address = 0x0000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x36.toByte(), 0x05.toByte(), 0xFF.toByte()),
            displacement = 0x05.toByte(),
            sourceByte = 0xFF.toByte()
        )

        assertEquals("LD (IX+05h), FFh", instruction.toString())
    }
}
