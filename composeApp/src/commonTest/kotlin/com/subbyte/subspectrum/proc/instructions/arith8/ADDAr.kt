package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ADDArTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ADDAr.decode(0x81L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x81.toByte(), instruction.bytes[0])

        val addAr = instruction as ADDAr
        assertEquals(RegisterCode.C, addAr.sourceRegister)
    }

    @Test
    fun executeAddRegisters() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setRegister(RegisterCode.C, 0x20.toByte())

        val instruction = ADDAr(
            address = 0x1000u,
            bytes = byteArrayOf(0x81.toByte()),
            sourceRegister = RegisterCode.C
        )

        instruction.execute()

        assertEquals(0x30.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = ADDAr(
            address = 0x0000u,
            bytes = byteArrayOf(0x81.toByte()),
            sourceRegister = RegisterCode.C
        )

        assertEquals("ADD A, C", instruction.toString())
    }
}
