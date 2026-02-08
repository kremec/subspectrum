package com.subbyte.subspectrum.proc.instructions.io

import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.*

class INITest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
        IO.ioPortSet.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = INI.decode(0xEDA2.toLong(), 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0xA2.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeInputIncrement() {
        // Set up registers
        Registers.registerSet.setB(0x05.toByte())
        Registers.registerSet.setC(0x01.toByte())
        Registers.registerSet.setHL(0x1000.toShort())

        // Set up IO port
        IO.ioPortSet.setIOPort(0x01u, 0x42.toByte())

        val instruction = INI(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0xA2.toByte()))
        )

        instruction.execute()

        // Verify B decremented
        assertEquals(0x04.toByte(), Registers.registerSet.getB())
        // Verify HL incremented
        assertEquals(0x1001.toShort(), Registers.registerSet.getHL())
        // Verify data stored at original HL
        assertEquals(0x42.toByte(), Memory.memorySet.getMemoryCell(0x1000u))
        // Z flag should be false (B != 0)
        assertFalse(Registers.registerSet.getZFlag())
        // N flag should be true
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeInputIncrementBBecomesZero() {
        // Set up registers with B = 1
        Registers.registerSet.setB(0x01.toByte())
        Registers.registerSet.setC(0x02.toByte())
        Registers.registerSet.setHL(0x2000.toShort())

        // Set up IO port
        IO.ioPortSet.setIOPort(0x02u, 0xAA.toByte())

        val instruction = INI(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0xA2.toByte()))
        )

        instruction.execute()

        // Verify B became 0
        assertEquals(0x00.toByte(), Registers.registerSet.getB())
        // Verify HL incremented
        assertEquals(0x2001.toShort(), Registers.registerSet.getHL())
        // Verify data stored at original HL
        assertEquals(0xAA.toByte(), Memory.memorySet.getMemoryCell(0x2000u))
        // Z flag should be true (B == 0)
        assertTrue(Registers.registerSet.getZFlag())
        // N flag should be true
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = INI(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0xA2.toByte()))
        )

        assertEquals("INI", instruction.toString())
    }
}
