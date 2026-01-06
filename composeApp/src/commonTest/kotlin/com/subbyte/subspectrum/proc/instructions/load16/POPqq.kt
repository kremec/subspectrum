package com.subbyte.subspectrum.proc.instructions.load16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterPairStackCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class POPqqTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = POPqq.decode(0xC1L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0xC1.toByte(), instruction.bytes[0])

        val instructionTyped = instruction as POPqq
        assertEquals(RegisterPairStackCode.BC, instructionTyped.destinationRegisterPairCode)
    }

    @Test
    fun executePopToBC() {
        Registers.specialPurposeRegisters.setSP(0x1234.toShort())
        Memory.memorySet.setMemoryCell(0x1234u, 0xCD.toByte())
        Memory.memorySet.setMemoryCell(0x1235u, 0xAB.toByte())

        val instruction = POPqq(
            address = 0x1000u,
            bytes = byteArrayOf(0xC1.toByte()),
            destinationRegisterPairCode = RegisterPairStackCode.BC
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Registers.registerSet.getB())
        assertEquals(0xCD.toByte(), Registers.registerSet.getC())
        assertEquals(0x1236.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executePopToDE() {
        Registers.specialPurposeRegisters.setSP(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2000u, 0x34.toByte())
        Memory.memorySet.setMemoryCell(0x2001u, 0x12.toByte())

        val instruction = POPqq(
            address = 0x1000u,
            bytes = byteArrayOf(0xD1.toByte()),
            destinationRegisterPairCode = RegisterPairStackCode.DE
        )

        instruction.execute()

        assertEquals(0x12.toByte(), Registers.registerSet.getD())
        assertEquals(0x34.toByte(), Registers.registerSet.getE())
        assertEquals(0x2002.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executePopToHL() {
        Registers.specialPurposeRegisters.setSP(0x3000.toShort())
        Memory.memorySet.setMemoryCell(0x3000u, 0xDC.toByte())
        Memory.memorySet.setMemoryCell(0x3001u, 0xFE.toByte())

        val instruction = POPqq(
            address = 0x1000u,
            bytes = byteArrayOf(0xE1.toByte()),
            destinationRegisterPairCode = RegisterPairStackCode.HL
        )

        instruction.execute()

        assertEquals(0xFE.toByte(), Registers.registerSet.getH())
        assertEquals(0xDC.toByte(), Registers.registerSet.getL())
        assertEquals(0x3002.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executePopToAF() {
        Registers.specialPurposeRegisters.setSP(0x4000.toShort())
        Memory.memorySet.setMemoryCell(0x4000u, 0xFF.toByte())
        Memory.memorySet.setMemoryCell(0x4001u, 0xAA.toByte())

        val instruction = POPqq(
            address = 0x1000u,
            bytes = byteArrayOf(0xF1.toByte()),
            destinationRegisterPairCode = RegisterPairStackCode.AF
        )

        instruction.execute()

        assertEquals(0xAA.toByte(), Registers.registerSet.getA())
        assertEquals(0xFF.toByte(), Registers.registerSet.getF())
        assertEquals(0x4002.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun toStringFormat() {
        val instruction = POPqq(
            address = 0x0000u,
            bytes = byteArrayOf(0xC1.toByte()),
            destinationRegisterPairCode = RegisterPairStackCode.BC
        )

        assertEquals("PUSH BC", instruction.toString())
    }
}
