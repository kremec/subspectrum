package com.subbyte.subspectrum.proc.instructions.load16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterPairCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDddnnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDddnn.decode(0x01CDABL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0x01.toByte(), instruction.bytes[0])
        assertEquals(0xCD.toByte(), instruction.bytes[1])
        assertEquals(0xAB.toByte(), instruction.bytes[2])

        val ldnn = instruction as LDddnn
        assertEquals(RegisterPairCode.BC, ldnn.destinationRegisterPair)
        assertEquals(0xABCD.toShort(), ldnn.sourceWord)
    }

    @Test
    fun executeLoadImmediateToBC() {
        val instruction = LDddnn(
            address = 0x1000u,
            bytes = byteArrayOf(0x01, 0xCD.toByte(), 0xAB.toByte()),
            destinationRegisterPair = RegisterPairCode.BC,
            sourceWord = 0xABCD.toShort()
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Registers.registerSet.getB())
        assertEquals(0xCD.toByte(), Registers.registerSet.getC())
    }

    @Test
    fun executeLoadImmediateToDE() {
        val instruction = LDddnn(
            address = 0x1000u,
            bytes = byteArrayOf(0x11, 0x34.toByte(), 0x12.toByte()),
            destinationRegisterPair = RegisterPairCode.DE,
            sourceWord = 0x1234.toShort()
        )

        instruction.execute()

        assertEquals(0x12.toByte(), Registers.registerSet.getD())
        assertEquals(0x34.toByte(), Registers.registerSet.getE())
    }

    @Test
    fun executeLoadImmediateToHL() {
        val instruction = LDddnn(
            address = 0x1000u,
            bytes = byteArrayOf(0x21, 0xFE.toByte(), 0xDC.toByte()),
            destinationRegisterPair = RegisterPairCode.HL,
            sourceWord = 0xFEDC.toShort()
        )

        instruction.execute()

        assertEquals(0xFE.toByte(), Registers.registerSet.getH())
        assertEquals(0xDC.toByte(), Registers.registerSet.getL())
    }

    @Test
    fun executeLoadImmediateToSP() {
        val instruction = LDddnn(
            address = 0x1000u,
            bytes = byteArrayOf(0x31, 0xFF.toByte(), 0xFF.toByte()),
            destinationRegisterPair = RegisterPairCode.SP,
            sourceWord = 0xFFFF.toShort()
        )

        instruction.execute()

        assertEquals(0xFFFF.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun toStringFormat() {
        val instruction = LDddnn(
            address = 0x0000u,
            bytes = byteArrayOf(0x01, 0xCD.toByte(), 0xAB.toByte()),
            destinationRegisterPair = RegisterPairCode.BC,
            sourceWord = 0xABCD.toShort()
        )

        assertEquals("LD BC, ABCDh", instruction.toString())
    }
}
