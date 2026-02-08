package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDnnATest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDnnA.decode(0x321200L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0x32.toByte(), instruction.bytes[0])
        assertEquals(0x12.toByte(), instruction.bytes[1])
        assertEquals(0x00.toByte(), instruction.bytes[2])

        val ldnnA = instruction as LDnnA
        assertEquals(0x0012.toUShort(), ldnnA.destinationUWord)
    }

    @Test
    fun executeLoadAToMemory() {
        Registers.registerSet.setA(0xAB.toByte())

        val instruction = LDnnA(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0x32, 0x12, 0x00)),
            destinationUWord = 0x0012.toUShort()
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Memory.memorySet.getMemoryCell(0x0012u))
    }

    @Test
    fun toStringFormat() {
        val instruction = LDnnA(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0x32, 0x12, 0x00)),
            destinationUWord = 0x0012.toUShort()
        )

        assertEquals("LD (0012h), A", instruction.toString())
    }
}
