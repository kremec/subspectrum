package com.subbyte.subspectrum.proc.instructions.arith16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DECIXTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = DECIX.decode(0xDD002BL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0x00.toByte(), instruction.bytes[1])
        assertEquals(0x2B.toByte(), instruction.bytes[2])
    }

    @Test
    fun executeDecIX() {
        Registers.specialPurposeRegisters.setIX(0x1000.toShort())

        val instruction = DECIX(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x00.toByte(), 0x2B.toByte())
        )

        instruction.execute()

        assertEquals(0x0FFF.toShort(), Registers.specialPurposeRegisters.getIX())
    }

    @Test
    fun toStringFormat() {
        val instruction = DECIX(
            address = 0x0000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x00.toByte(), 0x2B.toByte())
        )

        assertEquals("DEC IX", instruction.toString())
    }
}
