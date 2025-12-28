package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDrIYdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDrIYd.decode(0xDD4605L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0x46.toByte(), instruction.bytes[1])
        assertEquals(0x05.toByte(), instruction.bytes[2])

        val ldrIYd = instruction as LDrIYd
        assertEquals(RegisterCode.B, ldrIYd.destinationRegister)
        assertEquals(0x05.toByte(), ldrIYd.displacement)
    }

    @Test
    fun executeLoadMemoryIYToRegister() {
        Registers.specialPurposeRegisters.setIY(0x2000)
        Memory.memorySet.setMemoryCell(0x2005u, 0xAB.toByte())
        Registers.registerSet.setB(0x00.toByte())

        val instruction = LDrIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x46.toByte(), 0x05.toByte()),
            destinationRegister = RegisterCode.B,
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Registers.registerSet.getB())
    }

    @Test
    fun toStringFormat() {
        val instruction = LDrIYd(
            address = 0x0000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x46.toByte(), 0x05.toByte()),
            destinationRegister = RegisterCode.B,
            displacement = 0x05.toByte()
        )

        assertEquals("LD B, (IY+05h)", instruction.toString())
    }
}
