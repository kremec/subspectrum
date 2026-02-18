package com.subbyte.subspectrum.proc.instructions.io

import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class INAnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
        IO.ioPortSet.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = INAn.decode(0xDBA5L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xDB.toByte(), instruction.bytes[0])
        assertEquals(0xA5.toByte(), instruction.bytes[1])

        val inan = instruction as INAn
        assertEquals(0xA5.toUByte(), inan.sourceUByte)
    }

    @Test
    fun executeInputFromPort() {
        Registers.registerSet.setA(0x12.toByte())
        IO.ioPortSet.setIO(0x12A5u, 0x42.toByte())

        val instruction = INAn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xDB.toByte(), 0xA5.toByte())),
            sourceUByte = 0xA5.toUByte()
        )

        instruction.execute()

        assertEquals(0x42.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun executeInputFromPortZero() {
        Registers.registerSet.setA(0x34.toByte())
        IO.ioPortSet.setIO(0x3400u, 0xFF.toByte())

        val instruction = INAn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xDB.toByte(), 0x00.toByte())),
            sourceUByte = 0x00.toUByte()
        )

        instruction.execute()

        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun toStringFormat() {
        val instruction = INAn(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xDB.toByte(), 0xA5.toByte())),
            sourceUByte = 0xA5.toUByte()
        )

        assertEquals("IN A, (A5h)", instruction.toString())
    }
}
