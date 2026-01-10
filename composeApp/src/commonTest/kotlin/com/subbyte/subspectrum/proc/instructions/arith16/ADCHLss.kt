package com.subbyte.subspectrum.proc.instructions.arith16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterPairCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ADCHLssTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ADCHLss.decode(0xED4AL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0x4A.toByte(), instruction.bytes[1])

        val adcHlss = instruction as ADCHLss
        assertEquals(RegisterPairCode.BC, adcHlss.sourceRegisterPairCode)
    }

    @Test
    fun executeAdcHLBCWithNoCarry() {
        Registers.registerSet.setHL(0x1000.toShort())
        Registers.registerSet.setBC(0x0F00.toShort())
        Registers.registerSet.setCFlag(false)

        val instruction = ADCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x4A.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x1F00.toShort(), Registers.registerSet.getHL())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeAdcHLDEWithCarry() {
        Registers.registerSet.setHL(0x1000.toShort())
        Registers.registerSet.setDE(0x0F00.toShort())
        Registers.registerSet.setCFlag(true)

        val instruction = ADCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x5A.toByte()),
            sourceRegisterPairCode = RegisterPairCode.DE
        )

        instruction.execute()

        assertEquals(0x1F01.toShort(), Registers.registerSet.getHL())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeAdcHLHL() {
        Registers.registerSet.setHL(0x1000.toShort())
        Registers.registerSet.setCFlag(false)

        val instruction = ADCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x6A.toByte()),
            sourceRegisterPairCode = RegisterPairCode.HL
        )

        instruction.execute()

        assertEquals(0x2000.toShort(), Registers.registerSet.getHL())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeAdcHLSP() {
        Registers.registerSet.setHL(0x1000.toShort())
        Registers.specialPurposeRegisters.setSP(0x0F00.toShort())
        Registers.registerSet.setCFlag(true)

        val instruction = ADCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x7A.toByte()),
            sourceRegisterPairCode = RegisterPairCode.SP
        )

        instruction.execute()

        assertEquals(0x1F01.toShort(), Registers.registerSet.getHL())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = ADCHLss(
            address = 0x0000u,
            bytes = byteArrayOf(0xED.toByte(), 0x4A.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        assertEquals("ADC HL, BC", instruction.toString())
    }
}
