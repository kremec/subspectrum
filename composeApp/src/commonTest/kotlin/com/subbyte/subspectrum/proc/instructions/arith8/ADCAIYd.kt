package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ADCAIYdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ADCAIYd.decode(0xFD8E05L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0xFD.toByte(), instruction.bytes[0])
        assertEquals(0x8E.toByte(), instruction.bytes[1])
        assertEquals(0x05.toByte(), instruction.bytes[2])

        val adcAIYd = instruction as ADCAIYd
        assertEquals(0x05.toByte(), adcAIYd.displacement)
    }

    @Test
    fun executeADCWithIYOffset() {
        Registers.registerSet.setA(0x10.toByte())
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x20.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = ADCAIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x8E.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertEquals(0x30.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = ADCAIYd(
            address = 0x0000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x8E.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        assertEquals("ADC A, (IY + 5)", instruction.toString())
    }
}
