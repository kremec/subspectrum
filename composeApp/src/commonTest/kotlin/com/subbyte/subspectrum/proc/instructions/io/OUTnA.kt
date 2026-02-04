package com.subbyte.subspectrum.proc.instructions.io

import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class OUTnATest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
        IO.ioPortSet.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = OUTnA.decode(0xD3A5L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xD3.toByte(), instruction.bytes[0])
        assertEquals(0xA5.toByte(), instruction.bytes[1])

        val outna = instruction as OUTnA
        assertEquals(0xA5.toByte(), outna.destinationByte)
    }

    @Test
    fun executeOutputToPort() {
        // Set up A register
        Registers.registerSet.setA(0x42.toByte())

        val instruction = OUTnA(
            address = 0x1000u,
            bytes = byteArrayOf(0xD3.toByte(), 0xA5.toByte()),
            destinationByte = 0xA5.toByte()
        )

        instruction.execute()

        assertEquals(0x42.toByte(), IO.ioPortSet.getIOPort(0xA5u))
    }

    @Test
    fun executeOutputToPortZero() {
        // Set up A register
        Registers.registerSet.setA(0xFF.toByte())

        val instruction = OUTnA(
            address = 0x1000u,
            bytes = byteArrayOf(0xD3.toByte(), 0x00.toByte()),
            destinationByte = 0x00.toByte()
        )

        instruction.execute()

        assertEquals(0xFF.toByte(), IO.ioPortSet.getIOPort(0x00u))
    }

    @Test
    fun toStringFormat() {
        val instruction = OUTnA(
            address = 0x0000u,
            bytes = byteArrayOf(0xD3.toByte(), 0xA5.toByte()),
            destinationByte = 0xA5.toByte()
        )

        assertEquals("OUT (A5h), A", instruction.toString())
    }
}
