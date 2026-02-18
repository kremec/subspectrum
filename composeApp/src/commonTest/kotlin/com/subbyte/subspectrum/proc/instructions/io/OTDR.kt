package com.subbyte.subspectrum.proc.instructions.io

import com.subbyte.subspectrum.base.IO
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OTDRTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
        IO.ioPortSet.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = OTDR.decode(0xEDBB.toLong(), 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0xBB.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeOutputDecrementRepeatBNotZero() {
        // Set up PC at instruction
        Registers.specialPurposeRegisters.setPC(0x1000.toShort())

        // Set up registers
        Registers.registerSet.setB(0x02.toByte())
        Registers.registerSet.setC(0x01.toByte())
        Registers.registerSet.setHL(0x1000.toShort())

        // Set up memory at HL
        Memory.memorySet.setMemoryCell(0x1000u, 0x42.toByte())

        val instruction = OTDR(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0xBB.toByte()))
        )

        instruction.execute()

        // Verify B decremented
        assertEquals(0x01.toByte(), Registers.registerSet.getB())
        // Verify HL decremented
        assertEquals(0x0FFF.toShort(), Registers.registerSet.getHL())
        assertEquals(0x42.toByte(), IO.ioPortSet.getIO(0x0201u))
        // PC should go back by 2 (repeat the instruction)
        assertEquals(0x0FFE.toShort(), Registers.specialPurposeRegisters.getPC())
        // Z flag should be true
        assertTrue(Registers.registerSet.getZFlag())
        // N flag should be true
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeOutputDecrementRepeatBZero() {
        // Set up PC at instruction
        Registers.specialPurposeRegisters.setPC(0x1000.toShort())

        // Set up registers with B = 1 (will become 0)
        Registers.registerSet.setB(0x01.toByte())
        Registers.registerSet.setC(0x02.toByte())
        Registers.registerSet.setHL(0x2000.toShort())

        // Set up memory at HL
        Memory.memorySet.setMemoryCell(0x2000u, 0xAA.toByte())

        val instruction = OTDR(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0xBB.toByte()))
        )

        instruction.execute()

        // Verify B became 0
        assertEquals(0x00.toByte(), Registers.registerSet.getB())
        // Verify HL decremented
        assertEquals(0x1FFF.toShort(), Registers.registerSet.getHL())
        assertEquals(0xAA.toByte(), IO.ioPortSet.getIO(0x0102u))
        // PC should continue (not go back)
        assertEquals(0x1000.toShort(), Registers.specialPurposeRegisters.getPC())
        // Z flag should be true
        assertTrue(Registers.registerSet.getZFlag())
        // N flag should be true
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = OTDR(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0xBB.toByte()))
        )

        assertEquals("OTDR", instruction.toString())
    }
}
