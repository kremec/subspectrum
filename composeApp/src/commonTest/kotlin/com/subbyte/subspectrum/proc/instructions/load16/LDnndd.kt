package com.subbyte.subspectrum.proc.instructions.load16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterPairCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDnnddTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDnndd.decode(0xED431234L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(4, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0x43.toByte(), instruction.bytes[1])
        assertEquals(0x12.toByte(), instruction.bytes[2])
        assertEquals(0x34.toByte(), instruction.bytes[3])

        val ldnn = instruction as LDnndd
        assertEquals(RegisterPairCode.BC, ldnn.sourceRegisterPairCode)
        assertEquals(0x3412.toShort(), ldnn.destinationWord)
    }

    @Test
    fun executeStoreBCToMemory() {
        Registers.registerSet.setB(0xAB.toByte())
        Registers.registerSet.setC(0xCD.toByte())

        val instruction = LDnndd(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x43.toByte(), 0x12.toByte(), 0x34.toByte()),
            destinationWord = 0x3412.toShort(),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Memory.memorySet.getMemoryCell(0x3412u))
        assertEquals(0xCD.toByte(), Memory.memorySet.getMemoryCell(0x3413u))
    }

    @Test
    fun executeStoreDEToMemory() {
        Registers.registerSet.setD(0x12.toByte())
        Registers.registerSet.setE(0x34.toByte())

        val instruction = LDnndd(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x53.toByte(), 0x00.toByte(), 0x20.toByte()),
            destinationWord = 0x2000.toShort(),
            sourceRegisterPairCode = RegisterPairCode.DE
        )

        instruction.execute()

        assertEquals(0x12.toByte(), Memory.memorySet.getMemoryCell(0x2000u))
        assertEquals(0x34.toByte(), Memory.memorySet.getMemoryCell(0x2001u))
    }

    @Test
    fun executeStoreHLToMemory() {
        Registers.registerSet.setH(0xFE.toByte())
        Registers.registerSet.setL(0xDC.toByte())

        val instruction = LDnndd(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x63.toByte(), 0x00.toByte(), 0x30.toByte()),
            destinationWord = 0x3000.toShort(),
            sourceRegisterPairCode = RegisterPairCode.HL
        )

        instruction.execute()

        assertEquals(0xFE.toByte(), Memory.memorySet.getMemoryCell(0x3000u))
        assertEquals(0xDC.toByte(), Memory.memorySet.getMemoryCell(0x3001u))
    }

    @Test
    fun executeStoreSPToMemory() {
        Registers.specialPurposeRegisters.setSP(0xFFFF.toShort())

        val instruction = LDnndd(
            address = 0x1000u,
            bytes = byteArrayOf(0xED.toByte(), 0x73.toByte(), 0x00.toByte(), 0x40.toByte()),
            destinationWord = 0x4000.toShort(),
            sourceRegisterPairCode = RegisterPairCode.SP
        )

        instruction.execute()

        assertEquals(0xFF.toByte(), Memory.memorySet.getMemoryCell(0x4000u))
        assertEquals(0xFF.toByte(), Memory.memorySet.getMemoryCell(0x4001u))
    }

    @Test
    fun toStringFormat() {
        val instruction = LDnndd(
            address = 0x0000u,
            bytes = byteArrayOf(0xED.toByte(), 0x43.toByte(), 0x12.toByte(), 0x34.toByte()),
            destinationWord = 0x3412.toShort(),
            sourceRegisterPairCode = RegisterPairCode.BC
        )

        assertEquals("LD (3412h), BC", instruction.toString())
    }
}
