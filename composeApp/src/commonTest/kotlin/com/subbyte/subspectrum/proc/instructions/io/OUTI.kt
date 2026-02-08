package com.subbyte.subspectrum.proc.instructions.io

import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.*

class OUTITest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
        IO.ioPortSet.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = OUTI.decode(0xEDA3.toLong(), 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0xA3.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeOutputIncrement() {
        // Set up registers
        Registers.registerSet.setB(0x05.toByte())
        Registers.registerSet.setC(0x01.toByte())
        Registers.registerSet.setHL(0x1000.toShort())

        // Set up memory at HL
        Memory.memorySet.setMemoryCell(0x1000u, 0x42.toByte())

        val instruction = OUTI(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0xA3.toByte()))
        )

        instruction.execute()

        // Verify B decremented
        assertEquals(0x04.toByte(), Registers.registerSet.getB())
        // Verify HL incremented
        assertEquals(0x1001.toShort(), Registers.registerSet.getHL())
        // Verify data written to IO port (C port number)
        assertEquals(0x42.toByte(), IO.ioPortSet.getIOPort(0x01u))
        // Z flag should be false (B != 0)
        assertFalse(Registers.registerSet.getZFlag())
        // N flag should be true
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeOutputIncrementBBecomesZero() {
        // Set up registers with B = 1
        Registers.registerSet.setB(0x01.toByte())
        Registers.registerSet.setC(0x02.toByte())
        Registers.registerSet.setHL(0x2000.toShort())

        // Set up memory at HL
        Memory.memorySet.setMemoryCell(0x2000u, 0xAA.toByte())

        val instruction = OUTI(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0xA3.toByte()))
        )

        instruction.execute()

        // Verify B became 0
        assertEquals(0x00.toByte(), Registers.registerSet.getB())
        // Verify HL incremented
        assertEquals(0x2001.toShort(), Registers.registerSet.getHL())
        // Verify data written to IO port
        assertEquals(0xAA.toByte(), IO.ioPortSet.getIOPort(0x02u))
        // Z flag should be true (B == 0)
        assertTrue(Registers.registerSet.getZFlag())
        // N flag should be true
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = OUTI(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0xA3.toByte()))
        )

        assertEquals("OUTI", instruction.toString())
    }
}
