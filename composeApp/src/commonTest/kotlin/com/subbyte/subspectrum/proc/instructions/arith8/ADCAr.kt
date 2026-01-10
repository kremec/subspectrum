package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ADCArTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ADCAr.decode(0x89L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x89.toByte(), instruction.bytes[0])

        val adcAr = instruction as ADCAr
        assertEquals(RegisterCode.C, adcAr.sourceRegister)
    }

    @Test
    fun executeADCWithCarrySet() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setRegister(RegisterCode.C, 0x20.toByte())
        Registers.registerSet.setCFlag(true)

        val instruction = ADCAr(
            address = 0x1000u,
            bytes = byteArrayOf(0x89.toByte()),
            sourceRegister = RegisterCode.C
        )

        instruction.execute()

        assertEquals(0x31.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeADCWithCarryClear() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setRegister(RegisterCode.C, 0x20.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = ADCAr(
            address = 0x1000u,
            bytes = byteArrayOf(0x89.toByte()),
            sourceRegister = RegisterCode.C
        )

        instruction.execute()

        assertEquals(0x30.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = ADCAr(
            address = 0x0000u,
            bytes = byteArrayOf(0x89.toByte()),
            sourceRegister = RegisterCode.C
        )

        assertEquals("ADC A, C", instruction.toString())
    }
}
