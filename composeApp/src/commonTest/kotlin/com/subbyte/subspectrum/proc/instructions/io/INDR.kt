package com.subbyte.subspectrum.proc.instructions.io

import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class INDRTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
        IO.ioPortSet.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = INDR.decode(0xEDBA.toLong(), 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0xBA.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeInputDecrementRepeatBNotZero() {
        // Set up PC at instruction
        Registers.specialPurposeRegisters.setPC(0x1000.toShort())

        // Set up registers
        Registers.registerSet.setB(0x02.toByte())
        Registers.registerSet.setC(0x01.toByte())
        Registers.registerSet.setHL(0x1000.toShort())

        IO.ioPortSet.setIO(0x0201u, 0x42.toByte())

        val instruction = INDR(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0xBA.toByte()))
        )

        instruction.execute()

        // Verify B decremented
        assertEquals(0x01.toByte(), Registers.registerSet.getB())
        // Verify HL decremented
        assertEquals(0x0FFF.toShort(), Registers.registerSet.getHL())
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
    fun executeInputDecrementRepeatBZero() {
        // Set up PC at instruction
        Registers.specialPurposeRegisters.setPC(0x1000.toShort())

        // Set up registers with B = 1 (will become 0)
        Registers.registerSet.setB(0x01.toByte())
        Registers.registerSet.setC(0x02.toByte())
        Registers.registerSet.setHL(0x2000.toShort())

        IO.ioPortSet.setIO(0x0102u, 0xAA.toByte())

        val instruction = INDR(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0xBA.toByte()))
        )

        instruction.execute()

        // Verify B became 0
        assertEquals(0x00.toByte(), Registers.registerSet.getB())
        // Verify HL decremented
        assertEquals(0x1FFF.toShort(), Registers.registerSet.getHL())
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
        val instruction = INDR(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0xBA.toByte()))
        )

        assertEquals("INDR", instruction.toString())
    }
}
