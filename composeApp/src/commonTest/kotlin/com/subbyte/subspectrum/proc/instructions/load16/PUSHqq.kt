package com.subbyte.subspectrum.proc.instructions.load16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterPairStackCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PUSHqqTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = PUSHqq.decode(0xC5L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0xC5.toByte(), instruction.bytes[0])

        val instructionTyped = instruction as PUSHqq
        assertEquals(RegisterPairStackCode.BC, instructionTyped.sourceRegisterPairCode)
    }

    @Test
    fun executePushBC() {
        Registers.specialPurposeRegisters.setSP(0x1234.toShort())
        Registers.registerSet.setB(0xAB.toByte())
        Registers.registerSet.setC(0xCD.toByte())

        val instruction = PUSHqq(
            address = 0x1000u,
            bytes = byteArrayOf(0xC5.toByte()),
            sourceRegisterPairCode = RegisterPairStackCode.BC
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Memory.memorySet.getMemoryCell(0x1233u))
        assertEquals(0xCD.toByte(), Memory.memorySet.getMemoryCell(0x1232u))
        assertEquals(0x1232.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executePushDE() {
        Registers.specialPurposeRegisters.setSP(0x2000.toShort())
        Registers.registerSet.setD(0x12.toByte())
        Registers.registerSet.setE(0x34.toByte())

        val instruction = PUSHqq(
            address = 0x1000u,
            bytes = byteArrayOf(0xD5.toByte()),
            sourceRegisterPairCode = RegisterPairStackCode.DE
        )

        instruction.execute()

        assertEquals(0x12.toByte(), Memory.memorySet.getMemoryCell(0x1FFFu))
        assertEquals(0x34.toByte(), Memory.memorySet.getMemoryCell(0x1FFEu))
        assertEquals(0x1FFE.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executePushHL() {
        Registers.specialPurposeRegisters.setSP(0x3000.toShort())
        Registers.registerSet.setH(0xFE.toByte())
        Registers.registerSet.setL(0xDC.toByte())

        val instruction = PUSHqq(
            address = 0x1000u,
            bytes = byteArrayOf(0xE5.toByte()),
            sourceRegisterPairCode = RegisterPairStackCode.HL
        )

        instruction.execute()

        assertEquals(0xFE.toByte(), Memory.memorySet.getMemoryCell(0x2FFFu))
        assertEquals(0xDC.toByte(), Memory.memorySet.getMemoryCell(0x2FFEu))
        assertEquals(0x2FFE.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executePushAF() {
        Registers.specialPurposeRegisters.setSP(0x4000.toShort())
        Registers.registerSet.setA(0xAA.toByte())
        Registers.registerSet.setF(0xFF.toByte())

        val instruction = PUSHqq(
            address = 0x1000u,
            bytes = byteArrayOf(0xF5.toByte()),
            sourceRegisterPairCode = RegisterPairStackCode.AF
        )

        instruction.execute()

        assertEquals(0xAA.toByte(), Memory.memorySet.getMemoryCell(0x3FFFu))
        assertEquals(0xFF.toByte(), Memory.memorySet.getMemoryCell(0x3FFEu))
        assertEquals(0x3FFE.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun toStringFormat() {
        val instruction = PUSHqq(
            address = 0x0000u,
            bytes = byteArrayOf(0xC5.toByte()),
            sourceRegisterPairCode = RegisterPairStackCode.BC
        )

        assertEquals("PUSH BC", instruction.toString())
    }
}
