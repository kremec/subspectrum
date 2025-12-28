package com.subbyte.subspectrum.proc

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ProcessorTest {
    private lateinit var processor: Processor

    @BeforeTest
    fun setup() {
        processor = Processor()
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun stepExecutesSingleInstruction() {
        // Setup: LD B, C (0x41) at address 0x0000
        Memory.memorySet.setMemoryCell(0x0000u, 0x41)
        Registers.specialPurposeRegisters.setPC(0x0000)
        Registers.registerSet.setC(0x99.toByte())
        Registers.registerSet.setB(0x00)

        processor.step()

        // B should now equal C
        assertEquals(0x99.toByte(), Registers.registerSet.getB())
        // PC should advance by 1 byte
        assertEquals(0x0001, Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun stepAdvancesProgramCounter() {
        // LD B, C at 0x1000
        Memory.memorySet.setMemoryCell(0x1000u, 0x41)
        Registers.specialPurposeRegisters.setPC(0x1000)

        processor.step()

        assertEquals(0x1001, Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun runMultipleInstructions() {
        // Program:
        // 0x0000: LD B, A (0x47) - copy A to B
        // 0x0001: LD C, B (0x48) - copy B to C
        // 0x0002: LD D, C (0x51) - copy C to D
        Memory.memorySet.setMemoryCells(0x0000u, byteArrayOf(0x47, 0x48, 0x51))
        Registers.specialPurposeRegisters.setPC(0x0000)
        Registers.registerSet.setA(0x42)

        processor.run(3)

        // All registers should now be 0x42
        assertEquals(0x42, Registers.registerSet.getB())
        assertEquals(0x42, Registers.registerSet.getC())
        assertEquals(0x42, Registers.registerSet.getD())
        // PC should be at 0x0003
        assertEquals(0x0003, Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executesInstructionSequence() {
        // Setup a sequence: LD E, A; LD H, E
        // LD E, A: 01 011 111 = 0x5F
        // LD H, E: 01 100 011 = 0x63
        Memory.memorySet.setMemoryCells(0x2000u, byteArrayOf(0x5F, 0x63))
        Registers.specialPurposeRegisters.setPC(0x2000)
        Registers.registerSet.setA(0x7F)

        processor.step() // LD E, A
        assertEquals(0x7F, Registers.registerSet.getE())
        assertEquals(0x2001, Registers.specialPurposeRegisters.getPC())

        processor.step() // LD H, E
        assertEquals(0x7F, Registers.registerSet.getH())
        assertEquals(0x2002, Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun pcWrapsAroundAt64K() {
        // LD B, A at 0xFFFF
        Memory.memorySet.setMemoryCell(0xFFFFu, 0x47)
        Registers.specialPurposeRegisters.setPC(0xFFFF.toShort())

        processor.step()

        // PC should wrap to 0x0000
        assertEquals(0x0000, Registers.specialPurposeRegisters.getPC())
    }
}