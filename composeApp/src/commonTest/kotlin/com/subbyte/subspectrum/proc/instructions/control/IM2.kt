package com.subbyte.subspectrum.proc.instructions.control

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class IM2Test {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
        Processor.interruptMode = 0
    }

    @Test
    fun decodeInstruction() {
        val instruction = IM2.decode(0xED5EL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0x5E.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeSetsInterruptMode2() {
        Processor.interruptMode = 0

        val instruction = IM2(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x5E.toByte()))
        )

        instruction.execute()

        assertEquals(2, Processor.interruptMode)
    }

    @Test
    fun executeAlreadyMode2() {
        Processor.interruptMode = 2

        val instruction = IM2(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x5E.toByte()))
        )

        instruction.execute()

        assertEquals(2, Processor.interruptMode)
    }

    @Test
    fun executeFromMode1() {
        Processor.interruptMode = 1

        val instruction = IM2(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x5E.toByte()))
        )

        instruction.execute()

        assertEquals(2, Processor.interruptMode)
    }

    @Test
    fun toStringFormat() {
        val instruction = IM2(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x5E.toByte()))
        )

        assertEquals("IM 2", instruction.toString())
    }
}
