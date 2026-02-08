package com.subbyte.subspectrum.proc.instructions.load16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterPairSSCode
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDddMEMnnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDddMEMnn.decode(0xED4B1234L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(4, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0x4B.toByte(), instruction.bytes[1])
        assertEquals(0x12.toByte(), instruction.bytes[2])
        assertEquals(0x34.toByte(), instruction.bytes[3])

        val ldnn = instruction as LDddMEMnn
        assertEquals(RegisterPairSSCode.BC, ldnn.destinationRegisterPair)
        assertEquals(0x3412.toUShort(), ldnn.sourceUWord)
    }

    @Test
    fun executeLoadMemoryToBC() {
        Memory.memorySet.setMemoryCell(0x1234u, 0xCD.toByte())
        Memory.memorySet.setMemoryCell(0x1235u, 0xAB.toByte())

        val instruction = LDddMEMnn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x4B.toByte(), 0x34.toByte(), 0x12.toByte())),
            destinationRegisterPair = RegisterPairSSCode.BC,
            sourceUWord = 0x1234.toUShort()
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Registers.registerSet.getB())
        assertEquals(0xCD.toByte(), Registers.registerSet.getC())
    }

    @Test
    fun executeLoadMemoryToDE() {
        Memory.memorySet.setMemoryCell(0x2000u, 0x34.toByte())
        Memory.memorySet.setMemoryCell(0x2001u, 0x12.toByte())

        val instruction = LDddMEMnn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x5B.toByte(), 0x00.toByte(), 0x20.toByte())),
            destinationRegisterPair = RegisterPairSSCode.DE,
            sourceUWord = 0x2000.toUShort()
        )

        instruction.execute()

        assertEquals(0x12.toByte(), Registers.registerSet.getD())
        assertEquals(0x34.toByte(), Registers.registerSet.getE())
    }

    @Test
    fun executeLoadMemoryToHL() {
        Memory.memorySet.setMemoryCell(0x3000u, 0xDC.toByte())
        Memory.memorySet.setMemoryCell(0x3001u, 0xFE.toByte())

        val instruction = LDddMEMnn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x6B.toByte(), 0x00.toByte(), 0x30.toByte())),
            destinationRegisterPair = RegisterPairSSCode.HL,
            sourceUWord = 0x3000.toUShort()
        )

        instruction.execute()

        assertEquals(0xFE.toByte(), Registers.registerSet.getH())
        assertEquals(0xDC.toByte(), Registers.registerSet.getL())
    }

    @Test
    fun executeLoadMemoryToSP() {
        Memory.memorySet.setMemoryCell(0x4000u, 0xFF.toByte())
        Memory.memorySet.setMemoryCell(0x4001u, 0xFF.toByte())

        val instruction = LDddMEMnn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x7B.toByte(), 0x00.toByte(), 0x40.toByte())),
            destinationRegisterPair = RegisterPairSSCode.SP,
            sourceUWord = 0x4000.toUShort()
        )

        instruction.execute()

        assertEquals(0xFFFF.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun toStringFormat() {
        val instruction = LDddMEMnn(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x4B.toByte(), 0x34.toByte(), 0x12.toByte())),
            destinationRegisterPair = RegisterPairSSCode.BC,
            sourceUWord = 0x1234.toUShort()
        )

        assertEquals("LD BC, (1234h)", instruction.toString())
    }
}
