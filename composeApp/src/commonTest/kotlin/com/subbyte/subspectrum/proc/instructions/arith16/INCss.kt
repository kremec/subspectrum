package com.subbyte.subspectrum.proc.instructions.arith16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterPairCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class INCssTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = INCss.decode(0x03L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0x03.toByte(), instruction.bytes[0])

        val incSs = instruction as INCss
        assertEquals(RegisterPairCode.BC, incSs.sourceRegisterPairCode)
    }

    @Test
    fun executeIncBC() {
        Registers.registerSet.setBC(0x1000.toShort())

        val instruction = INCss(
            address = 0x1000u,
            bytes = byteArrayOf(0x03.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0x1001.toShort(), Registers.registerSet.getBC())
    }

    @Test
    fun executeIncDE() {
        Registers.registerSet.setDE(0x1000.toShort())

        val instruction = INCss(
            address = 0x1000u,
            bytes = byteArrayOf(0x13.toByte()),
            sourceRegisterPairCode = RegisterPairCode.DE
        )

        instruction.execute()

        assertEquals(0x1001.toShort(), Registers.registerSet.getDE())
    }

    @Test
    fun executeIncHL() {
        Registers.registerSet.setHL(0x1000.toShort())

        val instruction = INCss(
            address = 0x1000u,
            bytes = byteArrayOf(0x23.toByte()),
            sourceRegisterPairCode = RegisterPairCode.HL
        )

        instruction.execute()

        assertEquals(0x1001.toShort(), Registers.registerSet.getHL())
    }

    @Test
    fun executeIncSP() {
        Registers.specialPurposeRegisters.setSP(0x1000.toShort())

        val instruction = INCss(
            address = 0x1000u,
            bytes = byteArrayOf(0x33.toByte()),
            sourceRegisterPairCode = RegisterPairCode.SP
        )

        instruction.execute()

        assertEquals(0x1001.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun toStringFormat() {
        val instruction = INCss(
            address = 0x0000u,
            bytes = byteArrayOf(0x03.toByte()),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        assertEquals("INC BC", instruction.toString())
    }
}
