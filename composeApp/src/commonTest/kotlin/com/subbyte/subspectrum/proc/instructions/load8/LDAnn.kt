package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDAnnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDAnn.decode(0x3A0034L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0x3A.toByte(), instruction.bytes[0])
        assertEquals(0x00.toByte(), instruction.bytes[1])
        assertEquals(0x34.toByte(), instruction.bytes[2])

        val ldAnn = instruction as LDAnn
        assertEquals(0x0034, ldAnn.sourceWord)
    }

    @Test
    fun executeLoadMemoryToA() {
        Memory.memorySet.setMemoryCell(0x0034u, 0xCD.toByte())
        Registers.registerSet.setA(0x00)

        val instruction = LDAnn(
            address = 0x1000u,
            bytes = byteArrayOf(0x3A, 0x00, 0x34),
            sourceWord = 0x0034
        )

        instruction.execute()

        assertEquals(0xCD.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun toStringFormat() {
        val instruction = LDAnn(
            address = 0x0000u,
            bytes = byteArrayOf(0x3A, 0x00, 0x34),
            sourceWord = 0x0034
        )

        assertEquals("LD A, (0034h)", instruction.toString())
    }
}
