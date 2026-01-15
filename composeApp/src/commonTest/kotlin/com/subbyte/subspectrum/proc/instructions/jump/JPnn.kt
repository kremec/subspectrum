package com.subbyte.subspectrum.proc.instructions.jump

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class JPnnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val word = (0xC3L shl 16) or (0x1234L) // C3 12 34
        val instruction = JPnn.decode(word, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0xC3.toByte(), instruction.bytes[0])
        assertEquals(0x12.toByte(), instruction.bytes[1])
        assertEquals(0x34.toByte(), instruction.bytes[2])

        val jpnn = instruction as JPnn
        assertEquals(0x3412u, jpnn.targetAddress)
    }

    @Test
    fun executeJump() {
        Registers.specialPurposeRegisters.setPC(0x1000)

        val instruction = JPnn(
            address = 0x1000u,
            bytes = byteArrayOf(0xC3.toByte(), 0x56.toByte(), 0x78.toByte()),
            targetAddress = 0x5678u
        )

        instruction.execute()

        assertEquals(0x5678, Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeJumpToZero() {
        Registers.specialPurposeRegisters.setPC(0x1000)

        val instruction = JPnn(
            address = 0x1000u,
            bytes = byteArrayOf(0xC3.toByte(), 0x00.toByte(), 0x00.toByte()),
            targetAddress = 0x0000u
        )

        instruction.execute()

        assertEquals(0x0000, Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeJumpToHighAddress() {
        Registers.specialPurposeRegisters.setPC(0x1000)

        val instruction = JPnn(
            address = 0x1000u,
            bytes = byteArrayOf(0xC3.toByte(), 0xFF.toByte(), 0xFF.toByte()),
            targetAddress = 0xFFFFu
        )

        instruction.execute()

        assertEquals(0xFFFF.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun toStringFormat() {
        val instruction = JPnn(
            address = 0x0000u,
            bytes = byteArrayOf(0xC3.toByte(), 0x12.toByte(), 0x34.toByte()),
            targetAddress = 0x1234u
        )

        assertEquals("JP 1234h", instruction.toString())
    }

    @Test
    fun toStringFormatZero() {
        val instruction = JPnn(
            address = 0x0000u,
            bytes = byteArrayOf(0xC3.toByte(), 0x00.toByte(), 0x00.toByte()),
            targetAddress = 0x0000u
        )

        assertEquals("JP 0000h", instruction.toString())
    }
}
