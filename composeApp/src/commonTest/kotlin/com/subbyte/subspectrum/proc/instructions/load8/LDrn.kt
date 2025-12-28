package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDrnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDrn.decode(0x06ABL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0x06.toByte(), instruction.bytes[0])
        assertEquals(0xAB.toByte(), instruction.bytes[1])

        val ldrn = instruction as LDrn
        assertEquals(RegisterCode.B, ldrn.destinationRegister)
        assertEquals(0xAB.toByte(), ldrn.sourceByte)
    }

    @Test
    fun executeLoadImmediateToRegister() {
        Registers.registerSet.setB(0x00.toByte())

        val instruction = LDrn(
            address = 0x1000u,
            bytes = byteArrayOf(0x06.toByte(), 0xFF.toByte()),
            destinationRegister = RegisterCode.B,
            sourceByte = 0xFF.toByte()
        )

        instruction.execute()

        assertEquals(0xFF.toByte(), Registers.registerSet.getB())
    }

    @Test
    fun toStringFormat() {
        val instruction = LDrn(
            address = 0x0000u,
            bytes = byteArrayOf(0x06.toByte(), 0xFF.toByte()),
            destinationRegister = RegisterCode.B,
            sourceByte = 0xFF.toByte()
        )

        assertEquals("LD B, FFh", instruction.toString())
    }
}
