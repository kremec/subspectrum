package com.subbyte.subspectrum.proc.instructions.call

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RETNTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val word = (0xEDL shl 8) or 0x45L
        val instruction = RETN.decode(word, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0x45.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeReturnFromNonmaskableInterrupt() {
        Registers.specialPurposeRegisters.setSP(0xFFFC.toShort())
        Memory.memorySet.setMemoryCell(0xFFFCu, 0x45.toByte())
        Memory.memorySet.setMemoryCell(0xFFFDu, 0x1A.toByte())

        val instruction = RETN(
            address = 0x0066u,
            bytes = byteArrayOf(0xED.toByte(), 0x45.toByte())
        )

        instruction.execute()

        assertEquals(0x1A45.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFE.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeReturnToZeroAddress() {
        Registers.specialPurposeRegisters.setSP(0x8000.toShort())
        Memory.memorySet.setMemoryCell(0x8000u, 0x00.toByte())
        Memory.memorySet.setMemoryCell(0x8001u, 0x00.toByte())

        val instruction = RETN(
            address = 0x0066u,
            bytes = byteArrayOf(0xED.toByte(), 0x45.toByte())
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

        val instruction = RETN(
            address = 0x0066u,
            bytes = byteArrayOf(0xED.toByte(), 0x45.toByte())
        )

        instruction.execute()

        assertEquals(0xFFFF.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xC002.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeNMIScenarioFromDocumentation() {
        Registers.specialPurposeRegisters.setSP(0x1000.toShort())
        Memory.memorySet.setMemoryCell(0x0FFEu, 0x45.toByte())
        Memory.memorySet.setMemoryCell(0x0FFFu, 0x1A.toByte())
        Registers.specialPurposeRegisters.setSP(0x0FFE.toShort())

        val retn = RETN(
            address = 0x0066u,
            bytes = byteArrayOf(0xED.toByte(), 0x45.toByte())
        )
        retn.execute()

        assertEquals(0x1A45.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0x1000.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeMultipleNMIReturns() {
        Registers.specialPurposeRegisters.setPC(0x1003.toShort())
        Registers.specialPurposeRegisters.setSP(0xFFFE.toShort())

        val call1 = CALLnn(
            address = 0x1000u,
            bytes = byteArrayOf(0xCD.toByte(), 0x66.toByte(), 0x00.toByte()),
            targetAddress = 0x0066u
        )
        call1.execute()

        assertEquals(0x0066.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFC.toShort(), Registers.specialPurposeRegisters.getSP())

        Registers.specialPurposeRegisters.setPC(0x0069.toShort())
        val call2 = CALLnn(
            address = 0x0066u,
            bytes = byteArrayOf(0xCD.toByte(), 0x66.toByte(), 0x00.toByte()),
            targetAddress = 0x0066u
        )
        call2.execute()

        assertEquals(0x0066.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFA.toShort(), Registers.specialPurposeRegisters.getSP())

        val retn1 = RETN(
            address = 0x0066u,
            bytes = byteArrayOf(0xED.toByte(), 0x45.toByte())
        )
        retn1.execute()

        assertEquals(0x0069.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFC.toShort(), Registers.specialPurposeRegisters.getSP())

        val retn2 = RETN(
            address = 0x0069u,
            bytes = byteArrayOf(0xED.toByte(), 0x45.toByte())
        )
        retn2.execute()

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

            val instruction = RETN(
                address = 0x0066u,
                bytes = byteArrayOf(0xED.toByte(), 0x45.toByte())
            )
            
            instruction.execute()
            
            assertEquals(expected, Registers.specialPurposeRegisters.getPC())
            assertEquals((sp + 2).toShort(), Registers.specialPurposeRegisters.getSP())
        }
    }

    @Test
    fun toStringFormat() {
        val instruction = RETN(
            address = 0x0000u,
            bytes = byteArrayOf(0xED.toByte(), 0x45.toByte())
        )

        assertEquals("RETN", instruction.toString())
    }
}
