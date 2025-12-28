package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDRATest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDRA.decode(0xED4FL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0x4F.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeLoadAToR() {
        Registers.registerSet.setA(0xAB.toByte())
        Registers.specialPurposeRegisters.setR(0x00.toByte())

        val instruction = LDRA(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x4F.toByte())
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Registers.specialPurposeRegisters.getR())
    }

    @Test
    fun toStringFormat() {
        val instruction = LDRA(
            address = 0x0000u,
            bytes = byteArrayOf(0xED.toByte(), 0x4F.toByte())
        )

        assertEquals("LD R, A", instruction.toString())
    }
}
