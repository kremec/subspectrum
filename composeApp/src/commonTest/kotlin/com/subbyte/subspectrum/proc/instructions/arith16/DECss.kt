package com.subbyte.subspectrum.proc.instructions.arith16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterPairSSCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DECssTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = DECss.decode(0x0BL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x0B.toByte(), instruction.bytes[0])

        val decSs = instruction as DECss
        assertEquals(RegisterPairSSCode.BC, decSs.sourceRegisterPairCode)
    }

    @Test
    fun executeDecBC() {
        Registers.registerSet.setBC(0x1000.toShort())

        val instruction = DECss(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0x0B.toByte())),
            sourceRegisterPairCode = RegisterPairSSCode.BC
        )

        instruction.execute()

        assertEquals(0x0FFF.toShort(), Registers.registerSet.getBC())
    }

    @Test
    fun executeDecDE() {
        Registers.registerSet.setDE(0x1000.toShort())

        val instruction = DECss(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0x1B.toByte())),
            sourceRegisterPairCode = RegisterPairSSCode.DE
        )

        instruction.execute()

        assertEquals(0x0FFF.toShort(), Registers.registerSet.getDE())
    }

    @Test
    fun executeDecHL() {
        Registers.registerSet.setHL(0x1000.toShort())

        val instruction = DECss(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0x2B.toByte())),
            sourceRegisterPairCode = RegisterPairSSCode.HL
        )

        instruction.execute()

        assertEquals(0x0FFF.toShort(), Registers.registerSet.getHL())
    }

    @Test
    fun executeDecSP() {
        Registers.specialPurposeRegisters.setSP(0x1000.toShort())

        val instruction = DECss(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0x3B.toByte())),
            sourceRegisterPairCode = RegisterPairSSCode.SP
        )

        instruction.execute()

        assertEquals(0x0FFF.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun toStringFormat() {
        val instruction = DECss(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0x0B.toByte())),
            sourceRegisterPairCode = RegisterPairSSCode.BC
        )

        assertEquals("DEC BC", instruction.toString())
    }
}
