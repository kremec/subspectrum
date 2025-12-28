package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDACTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDABC.decode(0x0AL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x0A.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeLoadMemoryBCToA() {
        Memory.memorySet.setMemoryCell(0x2000u, 0xAB.toByte())
        Registers.registerSet.setBC(0x2000)
        Registers.registerSet.setA(0x00)

        val instruction = LDABC(
            address = 0x1000u,
            bytes = byteArrayOf(0x0A)
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun toStringFormat() {
        val instruction = LDABC(
            address = 0x0000u,
            bytes = byteArrayOf(0x0A)
        )

        assertEquals("LD A, (BC)", instruction.toString())
    }
}
