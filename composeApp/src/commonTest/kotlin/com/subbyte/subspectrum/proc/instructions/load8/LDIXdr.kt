package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDIXdrTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDIXdr.decode(0xDD7005L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0x70.toByte(), instruction.bytes[1])
        assertEquals(0x05.toByte(), instruction.bytes[2])

        val ldIXdr = instruction as LDIXdr
        assertEquals(RegisterCode.B, ldIXdr.sourceRegister)
        assertEquals(0x05.toByte(), ldIXdr.displacement)
    }

    @Test
    fun executeLoadRegisterToMemoryIX() {
        Registers.specialPurposeRegisters.setIX(0x2000)
        Registers.registerSet.setB(0xAB.toByte())

        val instruction = LDIXdr(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x70.toByte(), 0x05.toByte()),
            sourceRegister = RegisterCode.B,
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Memory.memorySet.getMemoryCell(0x2005u))
    }

    @Test
    fun toStringFormat() {
        val instruction = LDIXdr(
            address = 0x0000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x70.toByte(), 0x05.toByte()),
            sourceRegister = RegisterCode.B,
            displacement = 0x05.toByte()
        )

        assertEquals("LD (IX+05h), B", instruction.toString())
    }
}
