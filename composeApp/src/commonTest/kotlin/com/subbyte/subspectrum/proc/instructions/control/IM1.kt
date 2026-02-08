package com.subbyte.subspectrum.proc.instructions.control

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class IM1Test {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
        Processor.interruptMode = 0
    }

    @Test
    fun decodeInstruction() {
        val instruction = IM1.decode(0xED56L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0x56.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeSetsInterruptMode1() {
        Processor.interruptMode = 0

        val instruction = IM1(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x56.toByte()))
        )

        instruction.execute()

        assertEquals(1, Processor.interruptMode)
    }

    @Test
    fun executeAlreadyMode1() {
        Processor.interruptMode = 1

        val instruction = IM1(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x56.toByte()))
        )

        instruction.execute()

        assertEquals(1, Processor.interruptMode)
    }

    @Test
    fun executeFromMode2() {
        Processor.interruptMode = 2

        val instruction = IM1(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x56.toByte()))
        )

        instruction.execute()

        assertEquals(1, Processor.interruptMode)
    }

    @Test
    fun toStringFormat() {
        val instruction = IM1(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x56.toByte()))
        )

        assertEquals("IM 1", instruction.toString())
    }
}
