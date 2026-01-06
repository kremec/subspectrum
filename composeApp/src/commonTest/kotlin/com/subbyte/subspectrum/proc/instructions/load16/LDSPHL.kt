package com.subbyte.subspectrum.proc.instructions.load16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDSPHLTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDSPHL.decode(0xF9L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0xF9.toByte(), instruction.bytes[0])

        val instructionTyped = instruction as LDSPHL
    }

    @Test
    fun executeLoadHLToSP() {
        Registers.registerSet.setHL(0xABCD.toShort())

        val instruction = LDSPHL(
            address = 0x1000u,
            bytes = byteArrayOf(0xF9.toByte())
        )

        instruction.execute()

        assertEquals(0xABCD.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun toStringFormat() {
        val instruction = LDSPHL(
            address = 0x0000u,
            bytes = byteArrayOf(0xF9.toByte())
        )

        assertEquals("LD SP, HL", instruction.toString())
    }
}
