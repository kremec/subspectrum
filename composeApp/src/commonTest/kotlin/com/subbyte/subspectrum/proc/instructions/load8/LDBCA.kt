package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDBCCTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDBCA.decode(0x02L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x02.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeLoadAToMemoryBC() {
        Registers.registerSet.setA(0xAB.toByte())
        Registers.registerSet.setBC(0x2000)

        val instruction = LDBCA(
            address = 0x1000u,
            bytes = byteArrayOf(0x02)
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Memory.memorySet.getMemoryCell(0x2000u))
    }

    @Test
    fun toStringFormat() {
        val instruction = LDBCA(
            address = 0x0000u,
            bytes = byteArrayOf(0x02)
        )

        assertEquals("LD (BC), A", instruction.toString())
    }
}
