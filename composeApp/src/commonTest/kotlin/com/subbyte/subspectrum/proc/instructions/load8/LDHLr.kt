package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDHLrTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDHLr.decode(0x70L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x70.toByte(), instruction.bytes[0])

        val ldHLr = instruction as LDHLr
        assertEquals(RegisterCode.B, ldHLr.sourceRegister)
    }

    @Test
    fun executeLoadRegisterToMemoryHL() {
        Registers.registerSet.setHL(0x2000)
        Registers.registerSet.setB(0xAB.toByte())

        val instruction = LDHLr(
            address = 0x1000u,
            bytes = byteArrayOf(0x70),
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Memory.memorySet.getMemoryCell(0x2000u))
    }

    @Test
    fun toStringFormat() {
        val instruction = LDHLr(
            address = 0x0000u,
            bytes = byteArrayOf(0x70),
            sourceRegister = RegisterCode.B
        )

        assertEquals("LD (HL), B", instruction.toString())
    }
}
