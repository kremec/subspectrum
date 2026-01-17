package com.subbyte.subspectrum.proc.instructions.call

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RETITest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val word = (0xEDL shl 8) or 0x4DL
        val instruction = RETI.decode(word, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0x4D.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeReturnFromInterrupt() {
        Registers.specialPurposeRegisters.setSP(0xFFFC.toShort())
        Memory.memorySet.setMemoryCell(0xFFFCu, 0x34.toByte())
        Memory.memorySet.setMemoryCell(0xFFFDu, 0x12.toByte())

        val instruction = RETI(
            address = 0x0038u,
            bytes = byteArrayOf(0xED.toByte(), 0x4D.toByte())
        )

        instruction.execute()

        assertEquals(0x1234.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFE.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeReturnToZeroAddress() {
        Registers.specialPurposeRegisters.setSP(0x8000.toShort())
        Memory.memorySet.setMemoryCell(0x8000u, 0x00.toByte())
        Memory.memorySet.setMemoryCell(0x8001u, 0x00.toByte())

        val instruction = RETI(
            address = 0x0038u,
            bytes = byteArrayOf(0xED.toByte(), 0x4D.toByte())
        )

        instruction.execute()

        assertEquals(0x0000.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0x8002.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeReturnToHighAddress() {
        Registers.specialPurposeRegisters.setSP(0xC000.toShort())
        Memory.memorySet.setMemoryCell(0xC000u, 0xFF.toByte())
        Memory.memorySet.setMemoryCell(0xC001u, 0xFF.toByte())

        val instruction = RETI(
            address = 0x0038u,
            bytes = byteArrayOf(0xED.toByte(), 0x4D.toByte())
        )

        instruction.execute()

        assertEquals(0xFFFF.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xC002.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeNestedInterruptHandling() {
        Registers.specialPurposeRegisters.setPC(0x1003.toShort())
        Registers.specialPurposeRegisters.setSP(0xFFFE.toShort())

        val call1 = CALLnn(
            address = 0x1000u,
            bytes = byteArrayOf(0xCD.toByte(), 0x38.toByte(), 0x00.toByte()),
            targetAddress = 0x0038u
        )
        call1.execute()

        assertEquals(0x0038.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFC.toShort(), Registers.specialPurposeRegisters.getSP())

        Registers.specialPurposeRegisters.setPC(0x003B.toShort())
        val call2 = CALLnn(
            address = 0x0038u,
            bytes = byteArrayOf(0xCD.toByte(), 0x66.toByte(), 0x00.toByte()),
            targetAddress = 0x0066u
        )
        call2.execute()

        assertEquals(0x0066.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFA.toShort(), Registers.specialPurposeRegisters.getSP())

        val reti1 = RETI(
            address = 0x0066u,
            bytes = byteArrayOf(0xED.toByte(), 0x4D.toByte())
        )
        reti1.execute()

        assertEquals(0x003B.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFC.toShort(), Registers.specialPurposeRegisters.getSP())

        val reti2 = RETI(
            address = 0x003Bu,
            bytes = byteArrayOf(0xED.toByte(), 0x4D.toByte())
        )
        reti2.execute()

        assertEquals(0x1003.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFE.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeReturnWithDifferentAddresses() {
        val testCases = listOf(
            Pair(0x1234u, 0x1234.toShort()),
            Pair(0xABCDu, 0xABCD.toShort()),
            Pair(0x5678u, 0x5678.toShort()),
            Pair(0xFEDCu, 0xFEDC.toShort())
        )

        for ((address, expected) in testCases) {
            setup()
            
            val sp = 0xE000.toShort()
            Registers.specialPurposeRegisters.setSP(sp)
            
            val lowByte = (address.toInt() and 0xFF).toByte()
            val highByte = ((address.toInt() shr 8) and 0xFF).toByte()
            
            Memory.memorySet.setMemoryCell(sp.toUShort(), lowByte)
            Memory.memorySet.setMemoryCell((sp + 1).toUShort(), highByte)

            val instruction = RETI(
                address = 0x0038u,
                bytes = byteArrayOf(0xED.toByte(), 0x4D.toByte())
            )
            
            instruction.execute()
            
            assertEquals(expected, Registers.specialPurposeRegisters.getPC())
            assertEquals((sp + 2).toShort(), Registers.specialPurposeRegisters.getSP())
        }
    }

    @Test
    fun toStringFormat() {
        val instruction = RETI(
            address = 0x0000u,
            bytes = byteArrayOf(0xED.toByte(), 0x4D.toByte())
        )

        assertEquals("RETI", instruction.toString())
    }
}
