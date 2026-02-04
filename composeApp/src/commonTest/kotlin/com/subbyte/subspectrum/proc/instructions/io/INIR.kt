package com.subbyte.subspectrum.proc.instructions.io

import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class INIRTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
        IO.ioPortSet.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = INIR.decode(0xEDB2.toLong(), 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0xB2.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeInputIncrementRepeatBNotZero() {
        // Set up PC at instruction
        Registers.specialPurposeRegisters.setPC(0x1000.toShort())

        // Set up registers
        Registers.registerSet.setB(0x02.toByte())
        Registers.registerSet.setC(0x01.toByte())
        Registers.registerSet.setHL(0x1000.toShort())

        // Set up IO port
        IO.ioPortSet.setIOPort(0x01u, 0x42.toByte())

        val instruction = INIR(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0xB2.toByte())
        )

        instruction.execute()

        // Verify B decremented
        assertEquals(0x01.toByte(), Registers.registerSet.getB())
        // Verify HL incremented
        assertEquals(0x1001.toShort(), Registers.registerSet.getHL())
        // Verify data stored at original HL
        assertEquals(0x42.toByte(), Memory.memorySet.getMemoryCell(0x1000u))
        // PC should go back by 2 (repeat the instruction)
        assertEquals(0x0FFE.toShort(), Registers.specialPurposeRegisters.getPC())
        // Z flag should be true
        assertTrue(Registers.registerSet.getZFlag())
        // N flag should be true
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeInputIncrementRepeatBZero() {
        // Set up PC at instruction
        Registers.specialPurposeRegisters.setPC(0x1000.toShort())

        // Set up registers with B = 1 (will become 0)
        Registers.registerSet.setB(0x01.toByte())
        Registers.registerSet.setC(0x02.toByte())
        Registers.registerSet.setHL(0x2000.toShort())

        // Set up IO port
        IO.ioPortSet.setIOPort(0x02u, 0xAA.toByte())

        val instruction = INIR(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0xB2.toByte())
        )

        instruction.execute()

        // Verify B became 0
        assertEquals(0x00.toByte(), Registers.registerSet.getB())
        // Verify HL incremented
        assertEquals(0x2001.toShort(), Registers.registerSet.getHL())
        // Verify data stored at original HL
        assertEquals(0xAA.toByte(), Memory.memorySet.getMemoryCell(0x2000u))
        // PC should continue (not go back)
        assertEquals(0x1000.toShort(), Registers.specialPurposeRegisters.getPC())
        // Z flag should be true
        assertTrue(Registers.registerSet.getZFlag())
        // N flag should be true
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = INIR(
            address = 0x0000u,
            bytes = byteArrayOf(0xED.toByte(), 0xB2.toByte())
        )

        assertEquals("INIR", instruction.toString())
    }
}
