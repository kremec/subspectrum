package com.subbyte.subspectrum.proc.instructions.arith16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
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
        val instruction = DECIX.decode(0xDD2BL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0x2B.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeDecIX() {
        Registers.specialPurposeRegisters.setIX(0x1000.toShort())

        val instruction = DECIX(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xDD.toByte(), 0x2B.toByte()))
        )

        instruction.execute()

        assertEquals(0x0FFF.toShort(), Registers.specialPurposeRegisters.getIX())
    }

    @Test
    fun toStringFormat() {
        val instruction = DECIX(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xDD.toByte(), 0x2B.toByte()))
        )

        assertEquals("DEC IX", instruction.toString())
    }
}
