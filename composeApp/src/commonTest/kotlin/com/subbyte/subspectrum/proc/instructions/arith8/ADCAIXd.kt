package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ADCAIXdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ADCAIXd.decode(0xDD8E05L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0x8E.toByte(), instruction.bytes[1])
        assertEquals(0x05.toByte(), instruction.bytes[2])

        val adcAIXd = instruction as ADCAIXd
        assertEquals(0x05.toByte(), adcAIXd.displacement)
    }

    @Test
    fun executeADCWithIXOffset() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x20.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = ADCAIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x8E.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertEquals(0x30.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = ADCAIXd(
            address = 0x0000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x8E.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        assertEquals("ADC A, (IX + 5)", instruction.toString())
    }
}
