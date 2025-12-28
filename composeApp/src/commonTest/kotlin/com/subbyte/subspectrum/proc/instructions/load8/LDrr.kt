package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDrrTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDrr.decode(0x41L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x41.toByte(), instruction.bytes[0])

        val ldrr = instruction as LDrr
        assertEquals(RegisterCode.B, ldrr.destinationRegister)
        assertEquals(RegisterCode.C, ldrr.sourceRegister)
    }

    @Test
    fun executeLoadRegisterToRegister() {
        Registers.registerSet.setC(0x42)
        Registers.registerSet.setB(0x00)

        val instruction = LDrr(
            address = 0x1000u,
            bytes = byteArrayOf(0x41),
            destinationRegister = RegisterCode.B,
            sourceRegister = RegisterCode.C
        )

        instruction.execute()

        assertEquals(0x42, Registers.registerSet.getB())
        assertEquals(0x42, Registers.registerSet.getC())
    }

    @Test
    fun executeLoadAtoD() {
        Registers.registerSet.setA(0xFF.toByte())
        Registers.registerSet.setD(0x00)

        val instruction = LDrr(
            address = 0x2000u,
            bytes = byteArrayOf(0x57),
            destinationRegister = RegisterCode.D,
            sourceRegister = RegisterCode.A
        )

        instruction.execute()

        assertEquals(0xFF.toByte(), Registers.registerSet.getD())
        assertEquals(0xFF.toByte(), Registers.registerSet.getA())
    }

    @Test
    fun toStringFormat() {
        val instruction = LDrr(
            address = 0x0000u,
            bytes = byteArrayOf(0x41),
            destinationRegister = RegisterCode.B,
            sourceRegister = RegisterCode.C
        )

        assertEquals("LD B, C", instruction.toString())
    }
}
