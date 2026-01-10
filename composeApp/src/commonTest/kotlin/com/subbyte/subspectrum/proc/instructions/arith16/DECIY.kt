package com.subbyte.subspectrum.proc.instructions.arith16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DECIYTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = DECIY.decode(0xFD002BL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0xFD.toByte(), instruction.bytes[0])
        assertEquals(0x00.toByte(), instruction.bytes[1])
        assertEquals(0x2B.toByte(), instruction.bytes[2])
    }

    @Test
    fun executeDecIY() {
        Registers.specialPurposeRegisters.setIY(0x1000.toShort())

        val instruction = DECIY(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x00.toByte(), 0x2B.toByte())
        )

        instruction.execute()

        assertEquals(0x0FFF.toShort(), Registers.specialPurposeRegisters.getIY())
    }

    @Test
    fun toStringFormat() {
        val instruction = DECIY(
            address = 0x0000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x00.toByte(), 0x2B.toByte())
        )

        assertEquals("DEC IY", instruction.toString())
    }
}
