package com.subbyte.subspectrum.proc.instructions.io

import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class OUTCrTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
        IO.ioPortSet.reset()
    }

    @Test
    fun decodeInstructionFromA() {
        val instruction = OUTCr.decode(0xED79.toLong(), 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0x79.toByte(), instruction.bytes[1])

        val outcr = instruction as OUTCr
        assertEquals(RegisterCode.A, outcr.sourceRegister)
    }

    @Test
    fun decodeInstructionFromB() {
        val instruction = OUTCr.decode(0xED41.toLong(), 0x1000u)

        val outcr = instruction as OUTCr
        assertEquals(RegisterCode.B, outcr.sourceRegister)
    }

    @Test
    fun decodeInstructionFromC() {
        val instruction = OUTCr.decode(0xED49.toLong(), 0x1000u)

        val outcr = instruction as OUTCr
        assertEquals(RegisterCode.C, outcr.sourceRegister)
    }

    @Test
    fun executeOutputFromA() {
        // Set up registers
        Registers.registerSet.setA(0x42.toByte())
        Registers.registerSet.setC(0x01.toByte())

        val instruction = OUTCr(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x79.toByte())),
            sourceRegister = RegisterCode.A
        )

        instruction.execute()

        assertEquals(0x42.toByte(), IO.ioPortSet.getIOPort(0x01u))
    }

    @Test
    fun executeOutputFromB() {
        // Set up registers
        Registers.registerSet.setB(0x55.toByte())
        Registers.registerSet.setC(0x02.toByte())

        val instruction = OUTCr(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x41.toByte())),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x55.toByte(), IO.ioPortSet.getIOPort(0x02u))
    }

    @Test
    fun executeOutputFromD() {
        // Set up registers
        Registers.registerSet.setD(0xAA.toByte())
        Registers.registerSet.setC(0x03.toByte())

        val instruction = OUTCr(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x51.toByte())),
            sourceRegister = RegisterCode.D
        )

        instruction.execute()

        assertEquals(0xAA.toByte(), IO.ioPortSet.getIOPort(0x03u))
    }

    @Test
    fun toStringFormat() {
        val instruction = OUTCr(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x79.toByte())),
            sourceRegister = RegisterCode.A
        )

        assertEquals("OUT (C), A", instruction.toString())
    }
}
