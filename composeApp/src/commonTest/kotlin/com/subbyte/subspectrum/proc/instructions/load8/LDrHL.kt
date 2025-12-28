package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDrHLTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDrHL.decode(0x46L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x46.toByte(), instruction.bytes[0])

        val ldrHL = instruction as LDrHL
        assertEquals(RegisterCode.B, ldrHL.destinationRegister)
    }

    @Test
    fun executeLoadMemoryHLToRegister() {
        Registers.registerSet.setHL(0x2000)
        Memory.memorySet.setMemoryCell(0x2000u, 0xAB.toByte())
        Registers.registerSet.setB(0x00)

        val instruction = LDrHL(
            address = 0x1000u,
            bytes = byteArrayOf(0x46),
            destinationRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Registers.registerSet.getB())
    }

    @Test
    fun toStringFormat() {
        val instruction = LDrHL(
            address = 0x0000u,
            bytes = byteArrayOf(0x46),
            destinationRegister = RegisterCode.B
        )

        assertEquals("LD B, (HL)", instruction.toString())
    }
}
