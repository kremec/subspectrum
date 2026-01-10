package com.subbyte.subspectrum.proc.instructions.arith16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterPairCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SBCHLssTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = SBCHLss.decode(0xED42L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0x42.toByte(), instruction.bytes[1])

        val sbcHlss = instruction as SBCHLss
        assertEquals(RegisterPairCode.BC, sbcHlss.sourceRegisterPairCode)
    }

    @Test
    fun executeSbcHLBCWithNoCarry() {
        Registers.registerSet.setHL(0x2000.toShort())
        Registers.registerSet.setBC(0x0F00.toShort())
        Registers.registerSet.setCFlag(false)

        val instruction = SBCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x42.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x1100.toShort(), Registers.registerSet.getHL())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeSbcHLDEWithCarry() {
        Registers.registerSet.setHL(0x2000.toShort())
        Registers.registerSet.setDE(0x0F00.toShort())
        Registers.registerSet.setCFlag(true)

        val instruction = SBCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x52.toByte()),
            sourceRegisterPairCode = RegisterPairCode.DE
        )

        instruction.execute()

        assertEquals(0x10FF.toShort(), Registers.registerSet.getHL())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeSbcHLHL() {
        Registers.registerSet.setHL(0x1000.toShort())
        Registers.registerSet.setCFlag(false)

        val instruction = SBCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x62.toByte()),
            sourceRegisterPairCode = RegisterPairCode.HL
        )

        instruction.execute()

        assertEquals(0x0000.toShort(), Registers.registerSet.getHL())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeSbcHLSP() {
        Registers.registerSet.setHL(0x2000.toShort())
        Registers.specialPurposeRegisters.setSP(0x0F00.toShort())
        Registers.registerSet.setCFlag(true)

        val instruction = SBCHLss(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x72.toByte()),
            sourceRegisterPairCode = RegisterPairCode.SP
        )

        instruction.execute()

        assertEquals(0x10FF.toShort(), Registers.registerSet.getHL())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = SBCHLss(
            address = 0x0000u,
            bytes = byteArrayOf(0xED.toByte(), 0x42.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        assertEquals("SBC HL, BC", instruction.toString())
    }
}
