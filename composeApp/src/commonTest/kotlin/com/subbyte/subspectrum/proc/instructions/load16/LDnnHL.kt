package com.subbyte.subspectrum.proc.instructions.load16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDnnHLTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDnnHL.decode(0x221234L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0x22.toByte(), instruction.bytes[0])
        assertEquals(0x12.toByte(), instruction.bytes[1])
        assertEquals(0x34.toByte(), instruction.bytes[2])

        val ldnn = instruction as LDnnHL
        assertEquals(0x1234.toShort(), ldnn.destinationWord)
    }

    @Test
    fun executeStoreHLToMemory() {
        Registers.registerSet.setHL(0xABCD.toShort())

        val instruction = LDnnHL(
            address = 0x1000u,
            bytes = byteArrayOf(0x22, 0x34.toByte(), 0x12.toByte()),
            destinationWord = 0x1234.toShort()
        )

        instruction.execute()

        assertEquals(0xCD.toByte(), Memory.memorySet.getMemoryCell(0x1234u))
        assertEquals(0xAB.toByte(), Memory.memorySet.getMemoryCell(0x1235u))
    }

    @Test
    fun toStringFormat() {
        val instruction = LDnnHL(
            address = 0x0000u,
            bytes = byteArrayOf(0x22, 0x34.toByte(), 0x12.toByte()),
            destinationWord = 0x1234.toShort()
        )

        assertEquals("LD (1234h), HL", instruction.toString())
    }
}
