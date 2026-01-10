package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ADCAHLTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ADCAHL.decode(0x8EL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x8E.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeADCWithMemoryHL() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.registerSet.setHL(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x20.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = ADCAHL(
            address = 0x1000u,
            bytes = byteArrayOf(0x8E.toByte())
        )

        instruction.execute()

        assertEquals(0x30.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = ADCAHL(
            address = 0x0000u,
            bytes = byteArrayOf(0x8E.toByte())
        )

        assertEquals("ADC A, (HL)", instruction.toString())
    }
}
