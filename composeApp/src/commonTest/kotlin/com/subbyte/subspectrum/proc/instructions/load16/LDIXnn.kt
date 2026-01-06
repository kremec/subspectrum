package com.subbyte.subspectrum.proc.instructions.load16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDIXnnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDIXnn.decode(0xDD21CDABL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(4, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0x21.toByte(), instruction.bytes[1])
        assertEquals(0xCD.toByte(), instruction.bytes[2])
        assertEquals(0xAB.toByte(), instruction.bytes[3])

        val ldnn = instruction as LDIXnn
        assertEquals(0xABCD.toShort(), ldnn.sourceWord)
    }

    @Test
    fun executeLoadImmediateToIX() {
        val instruction = LDIXnn(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x21.toByte(), 0xCD.toByte(), 0xAB.toByte()),
            sourceWord = 0xABCD.toShort()
        )

        instruction.execute()

        assertEquals(0xABCD.toShort(), Registers.specialPurposeRegisters.getIX())
    }

    @Test
    fun toStringFormat() {
        val instruction = LDIXnn(
            address = 0x0000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x21.toByte(), 0xCD.toByte(), 0xAB.toByte()),
            sourceWord = 0xABCD.toShort()
        )

        assertEquals("LD IX, ABCDh", instruction.toString())
    }
}
