package com.subbyte.subspectrum.proc.instructions.load16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDnnIXTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDnnIX.decode(0xDD221234L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(4, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0x22.toByte(), instruction.bytes[1])
        assertEquals(0x12.toByte(), instruction.bytes[2])
        assertEquals(0x34.toByte(), instruction.bytes[3])

        val ldnn = instruction as LDnnIX
        assertEquals(0x3412.toShort(), ldnn.destinationWord)
    }

    @Test
    fun executeStoreIXToMemory() {
        Registers.specialPurposeRegisters.setIX(0xABCD.toShort())

        val instruction = LDnnIX(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x22.toByte(), 0x12.toByte(), 0x34.toByte()),
            destinationWord = 0x3412.toShort()
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Memory.memorySet.getMemoryCell(0x3412u))
        assertEquals(0xCD.toByte(), Memory.memorySet.getMemoryCell(0x3413u))
    }

    @Test
    fun toStringFormat() {
        val instruction = LDnnIX(
            address = 0x0000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x22.toByte(), 0x12.toByte(), 0x34.toByte()),
            destinationWord = 0x3412.toShort()
        )

        assertEquals("LD (3412h), IX", instruction.toString())
    }
}
