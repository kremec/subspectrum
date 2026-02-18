package com.subbyte.subspectrum.proc.instructions.io

import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.*

class INrCTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
        IO.ioPortSet.reset()
    }

    @Test
    fun decodeInstructionToA() {
        val instruction = INrC.decode(0xED78.toLong(), 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0x78.toByte(), instruction.bytes[1])

        val inrc = instruction as INrC
        assertEquals(RegisterCode.A, inrc.destinationRegister)
    }

    @Test
    fun decodeInstructionToB() {
        val instruction = INrC.decode(0xED40.toLong(), 0x1000u)

        val inrc = instruction as INrC
        assertEquals(RegisterCode.B, inrc.destinationRegister)
    }

    @Test
    fun decodeInstructionToC() {
        val instruction = INrC.decode(0xED48.toLong(), 0x1000u)

        val inrc = instruction as INrC
        assertEquals(RegisterCode.C, inrc.destinationRegister)
    }

    @Test
    fun executeInputToA() {
        Registers.registerSet.setB(0x12.toByte())
        Registers.registerSet.setC(0x01.toByte())

        IO.ioPortSet.setIO(0x1201u, 0x42.toByte())

        val instruction = INrC(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x78.toByte())),
            destinationRegister = RegisterCode.A
        )

        instruction.execute()

        assertEquals(0x42.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getSFlag())
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
        // Parity: 0x42 = 01000010, has 2 ones -> even parity -> true
        assertTrue(Registers.registerSet.getPVFlag())
    }

    @Test
    fun executeInputToB() {
        Registers.registerSet.setB(0x34.toByte())
        Registers.registerSet.setC(0x02.toByte())

        IO.ioPortSet.setIO(0x3402u, 0x80.toByte())

        val instruction = INrC(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x40.toByte())),
            destinationRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x80.toByte(), Registers.registerSet.getB())
        assertTrue(Registers.registerSet.getSFlag()) // Bit 7 is set
        assertFalse(Registers.registerSet.getZFlag())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
        // Parity: 0x80 = 10000000, has 1 one -> odd parity -> false
        assertFalse(Registers.registerSet.getPVFlag())
    }

    @Test
    fun executeInputZeroValue() {
        Registers.registerSet.setB(0x56.toByte())
        Registers.registerSet.setC(0x03.toByte())

        IO.ioPortSet.setIO(0x5603u, 0x00.toByte())

        val instruction = INrC(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x50.toByte())),
            destinationRegister = RegisterCode.D
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getD())
        assertFalse(Registers.registerSet.getSFlag())
        assertTrue(Registers.registerSet.getZFlag()) // Zero flag should be set
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
        // Parity: 0x00 = 00000000, has 0 ones -> even parity -> true
        assertTrue(Registers.registerSet.getPVFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = INrC(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x78.toByte())),
            destinationRegister = RegisterCode.A
        )

        assertEquals("IN A, (C)", instruction.toString())
    }
}
