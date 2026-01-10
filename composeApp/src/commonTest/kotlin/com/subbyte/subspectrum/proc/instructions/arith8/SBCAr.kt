package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SBCArTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = SBCAr.decode(0x99L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x99.toByte(), instruction.bytes[0])

        val sbcAr = instruction as SBCAr
        assertEquals(RegisterCode.C, sbcAr.sourceRegister)
    }

    @Test
    fun executeSBCWithCarrySet() {
        Registers.registerSet.setA(0x30.toByte())
        Registers.registerSet.setRegister(RegisterCode.C, 0x10.toByte())
        Registers.registerSet.setCFlag(true)

        val instruction = SBCAr(
            address = 0x1000u,
            bytes = byteArrayOf(0x99.toByte()),
            sourceRegister = RegisterCode.C
        )

        instruction.execute()

        assertEquals(0x1F.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeSBCWithCarryClear() {
        Registers.registerSet.setA(0x30.toByte())
        Registers.registerSet.setRegister(RegisterCode.C, 0x10.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = SBCAr(
            address = 0x1000u,
            bytes = byteArrayOf(0x99.toByte()),
            sourceRegister = RegisterCode.C
        )

        instruction.execute()

        assertEquals(0x20.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = SBCAr(
            address = 0x0000u,
            bytes = byteArrayOf(0x99.toByte()),
            sourceRegister = RegisterCode.C
        )

        assertEquals("SBC A, C", instruction.toString())
    }
}
