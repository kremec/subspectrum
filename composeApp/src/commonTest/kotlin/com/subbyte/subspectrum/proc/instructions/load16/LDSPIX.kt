package com.subbyte.subspectrum.proc.instructions.load16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDSPIXTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDSPIX.decode(0xDDF9L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0xF9.toByte(), instruction.bytes[1])

        val instructionTyped = instruction as LDSPIX
    }

    @Test
    fun executeLoadIXToSP() {
        Registers.specialPurposeRegisters.setIX(0xABCD.toShort())

        val instruction = LDSPIX(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xF9.toByte())
        )

        instruction.execute()

        assertEquals(0xABCD.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun toStringFormat() {
        val instruction = LDSPIX(
            address = 0x0000u,
            bytes = byteArrayOf(0xDD.toByte(), 0xF9.toByte())
        )

        assertEquals("LD SP, IX", instruction.toString())
    }
}
