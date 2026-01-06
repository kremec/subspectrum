package com.subbyte.subspectrum.proc.instructions.load16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDSPIYTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDSPIY.decode(0xFDF9L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xFD.toByte(), instruction.bytes[0])
        assertEquals(0xF9.toByte(), instruction.bytes[1])

        val instructionTyped = instruction as LDSPIY
    }

    @Test
    fun executeLoadIYToSP() {
        Registers.specialPurposeRegisters.setIY(0xABCD.toShort())

        val instruction = LDSPIY(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xF9.toByte())
        )

        instruction.execute()

        assertEquals(0xABCD.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun toStringFormat() {
        val instruction = LDSPIY(
            address = 0x0000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xF9.toByte())
        )

        assertEquals("LD SP, IY", instruction.toString())
    }
}
