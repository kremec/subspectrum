package com.subbyte.subspectrum.proc.instructions.call

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RETTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val word = 0xC9L
        val instruction = RET.decode(word, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0xC9.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeReturnFromSubroutine() {
        // Setup: SP points to stack with return address 0x1234
        Registers.specialPurposeRegisters.setSP(0xFFFC.toShort())
        Memory.memorySet.setMemoryCell(0xFFFCu, 0x34.toByte()) // Low byte
        Memory.memorySet.setMemoryCell(0xFFFDu, 0x12.toByte()) // High byte

        val instruction = RET(
            address = 0x5000u,
            bytes = byteArrayOf(0xC9.toByte())
        )

        instruction.execute()

        // PC should be set to return address
        assertEquals(0x1234.toShort(), Registers.specialPurposeRegisters.getPC())
        
        // SP should be incremented by 2
        assertEquals(0xFFFE.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeReturnToZeroAddress() {
        Registers.specialPurposeRegisters.setSP(0x8000.toShort())
        Memory.memorySet.setMemoryCell(0x8000u, 0x00.toByte())
        Memory.memorySet.setMemoryCell(0x8001u, 0x00.toByte())

        val instruction = RET(
            address = 0x2000u,
            bytes = byteArrayOf(0xC9.toByte())
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

        val instruction = RET(
            address = 0x3000u,
            bytes = byteArrayOf(0xC9.toByte())
        )

        instruction.execute()

        assertEquals(0xFFFF.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xC002.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeCallAndReturnSequence() {
        // Simulate a CALL followed by RET
        
        // Initial state: PC at 0x1000, SP at 0xFFFE
        Registers.specialPurposeRegisters.setPC(0x1003.toShort()) // After 3-byte CALL
        Registers.specialPurposeRegisters.setSP(0xFFFE.toShort())

        // CALL pushes return address onto stack
        val callInstruction = CALLnn(
            address = 0x1000u,
            bytes = byteArrayOf(0xCD.toByte(), 0x00.toByte(), 0x20.toByte()),
            targetAddress = 0x2000u
        )
        callInstruction.execute()

        // After CALL: PC should be at 0x2000, SP at 0xFFFC
        assertEquals(0x2000.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFC.toShort(), Registers.specialPurposeRegisters.getSP())

        // RET pops return address from stack
        val retInstruction = RET(
            address = 0x2000u,
            bytes = byteArrayOf(0xC9.toByte())
        )
        retInstruction.execute()

        // After RET: PC should be back at 0x1003, SP at 0xFFFE
        assertEquals(0x1003.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFE.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeNestedCallAndReturn() {
        // Test nested CALL/RET sequence
        
        // First CALL from 0x1000 to 0x2000
        Registers.specialPurposeRegisters.setPC(0x1003.toShort())
        Registers.specialPurposeRegisters.setSP(0xFFFE.toShort())
        
        val call1 = CALLnn(
            address = 0x1000u,
            bytes = byteArrayOf(0xCD.toByte(), 0x00.toByte(), 0x20.toByte()),
            targetAddress = 0x2000u
        )
        call1.execute()
        
        assertEquals(0x2000.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFC.toShort(), Registers.specialPurposeRegisters.getSP())

        // Second CALL from 0x2000 to 0x3000
        Registers.specialPurposeRegisters.setPC(0x2003.toShort())
        val call2 = CALLnn(
            address = 0x2000u,
            bytes = byteArrayOf(0xCD.toByte(), 0x00.toByte(), 0x30.toByte()),
            targetAddress = 0x3000u
        )
        call2.execute()
        
        assertEquals(0x3000.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFA.toShort(), Registers.specialPurposeRegisters.getSP())

        // First RET from 0x3000 back to 0x2003
        val ret1 = RET(
            address = 0x3000u,
            bytes = byteArrayOf(0xC9.toByte())
        )
        ret1.execute()
        
        assertEquals(0x2003.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFC.toShort(), Registers.specialPurposeRegisters.getSP())

        // Second RET from 0x2003 back to 0x1003
        val ret2 = RET(
            address = 0x2003u,
            bytes = byteArrayOf(0xC9.toByte())
        )
        ret2.execute()
        
        assertEquals(0x1003.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFE.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeReturnWithDifferentAddresses() {
        // Test various return addresses
        val testCases = listOf(
            Pair(0x1234u, 0x1234.toShort()),
            Pair(0xABCDu, 0xABCD.toShort()),
            Pair(0x5678u, 0x5678.toShort()),
            Pair(0xFEDCu, 0xFEDC.toShort())
        )

        for ((address, expected) in testCases) {
            setup() // Reset state
            
            val sp = 0xE000.toShort()
            Registers.specialPurposeRegisters.setSP(sp)
            
            val lowByte = (address.toInt() and 0xFF).toByte()
            val highByte = ((address.toInt() shr 8) and 0xFF).toByte()
            
            Memory.memorySet.setMemoryCell(sp.toUShort(), lowByte)
            Memory.memorySet.setMemoryCell((sp + 1).toUShort(), highByte)

            val instruction = RET(
                address = 0x4000u,
                bytes = byteArrayOf(0xC9.toByte())
            )
            
            instruction.execute()
            
            assertEquals(expected, Registers.specialPurposeRegisters.getPC())
            assertEquals((sp + 2).toShort(), Registers.specialPurposeRegisters.getSP())
        }
    }

    @Test
    fun toStringFormat() {
        val instruction = RET(
            address = 0x0000u,
            bytes = byteArrayOf(0xC9.toByte())
        )

        assertEquals("RET", instruction.toString())
    }
}
