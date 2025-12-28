package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDADETest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDADE.decode(0x0AL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x0A.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeLoadMemoryDEToA() {
        Memory.memorySet.setMemoryCell(0x2000u, 0xAB.toByte())
        Registers.registerSet.setDE(0x2000)
        Registers.registerSet.setA(0x00)

        val instruction = LDADE(
            address = 0x1000u,
            bytes = byteArrayOf(0x0A)
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun toStringFormat() {
        val instruction = LDADE(
            address = 0x0000u,
            bytes = byteArrayOf(0x0A)
        )

        assertEquals("LD A, (DE)", instruction.toString())
    }
}
