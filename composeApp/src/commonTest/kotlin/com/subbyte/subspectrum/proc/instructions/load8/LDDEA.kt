package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDDEATest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDDEA.decode(0x12L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x12.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeLoadAToMemoryDE() {
        Registers.registerSet.setA(0xAB.toByte())
        Registers.registerSet.setDE(0x2000)

        val instruction = LDDEA(
            address = 0x1000u,
            bytes = byteArrayOf(0x12)
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Memory.memorySet.getMemoryCell(0x2000u))
    }

    @Test
    fun toStringFormat() {
        val instruction = LDDEA(
            address = 0x0000u,
            bytes = byteArrayOf(0x12)
        )

        assertEquals("LD (DE), A", instruction.toString())
    }
}
