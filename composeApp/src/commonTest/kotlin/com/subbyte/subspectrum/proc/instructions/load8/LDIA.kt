package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDIATest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDIA.decode(0xED47L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0x47.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeLoadAToI() {
        Registers.registerSet.setA(0xAB.toByte())
        Registers.specialPurposeRegisters.setI(0x00.toByte())

        val instruction = LDIA(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x47.toByte())
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Registers.specialPurposeRegisters.getI())
    }

    @Test
    fun toStringFormat() {
        val instruction = LDIA(
            address = 0x0000u,
            bytes = byteArrayOf(0xED.toByte(), 0x47.toByte())
        )

        assertEquals("LD I, A", instruction.toString())
    }
}
