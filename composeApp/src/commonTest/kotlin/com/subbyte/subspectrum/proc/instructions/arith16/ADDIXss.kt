package com.subbyte.subspectrum.proc.instructions.arith16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterPairCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ADDIXssTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = ADDIXss.decode(0xDD09L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0x09.toByte(), instruction.bytes[1])

        val addIxss = instruction as ADDIXss
        assertEquals(RegisterPairCode.BC, addIxss.sourceRegisterPairCode)
    }

    @Test
    fun executeAddIXBC() {
        Registers.specialPurposeRegisters.setIX(0x1000.toShort())
        Registers.registerSet.setBC(0x0F00.toShort())

        val instruction = ADDIXss(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x1F00.toShort(), Registers.specialPurposeRegisters.getIX())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeAddIXDE() {
        Registers.specialPurposeRegisters.setIX(0x1000.toShort())
        Registers.registerSet.setDE(0x0F00.toShort())

        val instruction = ADDIXss(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x19.toByte()),
            sourceRegisterPairCode = RegisterPairCode.DE
        )

        instruction.execute()

        assertEquals(0x1F00.toShort(), Registers.specialPurposeRegisters.getIX())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeAddIXHL() {
        Registers.specialPurposeRegisters.setIX(0x1000.toShort())
        Registers.registerSet.setHL(0x0F00.toShort())

        val instruction = ADDIXss(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x29.toByte()),
            sourceRegisterPairCode = RegisterPairCode.HL
        )

        instruction.execute()

        assertEquals(0x1F00.toShort(), Registers.specialPurposeRegisters.getIX())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeAddIXSP() {
        Registers.specialPurposeRegisters.setIX(0x1000.toShort())
        Registers.specialPurposeRegisters.setSP(0x0F00.toShort())

        val instruction = ADDIXss(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x39.toByte()),
            sourceRegisterPairCode = RegisterPairCode.SP
        )

        instruction.execute()

        assertEquals(0x1F00.toShort(), Registers.specialPurposeRegisters.getIX())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = ADDIXss(
            address = 0x0000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x09.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        assertEquals("ADD IX, BC", instruction.toString())
    }
}
