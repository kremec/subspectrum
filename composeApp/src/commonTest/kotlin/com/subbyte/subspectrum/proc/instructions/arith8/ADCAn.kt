package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ADCAnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ADCAn.decode(0xCEABL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xCE.toByte(), instruction.bytes[0])
        assertEquals(0xAB.toByte(), instruction.bytes[1])

        val adcAn = instruction as ADCAn
        assertEquals(0xAB.toByte(), adcAn.sourceByte)
    }

    @Test
    fun executeADCImmediate() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = ADCAn(
            address = 0x1000u,
            bytes = byteArrayOf(0xCE.toByte(), 0x20.toByte()),
            sourceByte = 0x20.toByte()
        )

        instruction.execute()

        assertEquals(0x30.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = ADCAn(
            address = 0x0000u,
            bytes = byteArrayOf(0xCE.toByte(), 0xAB.toByte()),
            sourceByte = 0xAB.toByte()
        )

        assertEquals("ADC A, -85", instruction.toString())
    }
}
