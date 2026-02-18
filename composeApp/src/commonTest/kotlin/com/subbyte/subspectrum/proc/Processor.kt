package com.subbyte.subspectrum.proc

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.base.ULAKeyboard
import com.subbyte.subspectrum.base.ULATiming
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ProcessorTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
        ULATiming.reset()
        ULAKeyboard.releaseAllKeyboardKeys()
        Processor.reset()
    }

    @Test
    fun stepExecutesSingleInstruction() {
        // Setup: LD B, C (0x41) at address 0x0000
        Memory.memorySet.setMemoryCell(0x0000u, 0x41)
        Registers.specialPurposeRegisters.setPC(0x0000)
        Registers.registerSet.setC(0x99.toByte())
        Registers.registerSet.setB(0x00)

        Processor.step()

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

        Processor.step()

        assertEquals(0x1001, Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executesInstructionSequence() {
        // Setup a sequence: LD E, A; LD H, E
        // LD E, A: 01 011 111 = 0x5F
        // LD H, E: 01 100 011 = 0x63
        Memory.memorySet.setMemoryCells(0x2000u, byteArrayOf(0x5F, 0x63))
        Registers.specialPurposeRegisters.setPC(0x2000)
        Registers.registerSet.setA(0x7F)

        Processor.step() // LD E, A
        assertEquals(0x7F, Registers.registerSet.getE())
        assertEquals(0x2001, Registers.specialPurposeRegisters.getPC())

        Processor.step() // LD H, E
        assertEquals(0x7F, Registers.registerSet.getH())
        assertEquals(0x2002, Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun pcWrapsAroundAt64K() {
        // LD B, A at 0xFFFF
        Memory.memorySet.setMemoryCell(0xFFFFu, 0x47)
        Registers.specialPurposeRegisters.setPC(0xFFFF.toShort())

        Processor.step()

        // PC should wrap to 0x0000
        assertEquals(0x0000, Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun im1InterruptPushesPCAndJumpsTo0038() {
        Memory.memorySet.setMemoryCell(0x0000u, 0x00) // NOP
        Registers.specialPurposeRegisters.setPC(0x0000)
        Registers.specialPurposeRegisters.setSP(0xFFFE.toShort())

        Processor.interruptMode = 1
        Processor.IFF1 = true
        Processor.IFF2 = true

        ULATiming.advanceCycles(ULATiming.T_STATES_PER_FRAME)

        Processor.step()

        assertEquals(0x0038, Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFC.toShort(), Registers.specialPurposeRegisters.getSP())
        assertEquals(0x01.toByte(), Memory.memorySet.getMemoryCell(0xFFFCu))
        assertEquals(0x00.toByte(), Memory.memorySet.getMemoryCell(0xFFFDu))
        assertFalse(Processor.IFF1)
        assertFalse(Processor.IFF2)
    }

    @Test
    fun eiDelaysInterruptUntilAfterFollowingInstruction() {
        Memory.memorySet.setMemoryCells(0x0000u, byteArrayOf(0xFB.toByte(), 0x00.toByte())) // EI; NOP
        Registers.specialPurposeRegisters.setPC(0x0000)
        Registers.specialPurposeRegisters.setSP(0xFFFE.toShort())

        Processor.interruptMode = 1
        Processor.IFF1 = false
        Processor.IFF2 = false

        ULATiming.advanceCycles(ULATiming.T_STATES_PER_FRAME)

        Processor.step()
        assertEquals(0x0001, Registers.specialPurposeRegisters.getPC())

        Processor.step()
        assertEquals(0x0038, Registers.specialPurposeRegisters.getPC())
        assertEquals(0x02.toByte(), Memory.memorySet.getMemoryCell(0xFFFCu))
        assertEquals(0x00.toByte(), Memory.memorySet.getMemoryCell(0xFFFDu))
    }

    @Test
    fun interruptExitsHALTAndPushesNextInstructionAddress() {
        Memory.memorySet.setMemoryCell(0x0000u, 0x76.toByte()) // HALT
        Registers.specialPurposeRegisters.setPC(0x0000)
        Registers.specialPurposeRegisters.setSP(0xFFFE.toShort())

        Processor.interruptMode = 1
        Processor.IFF1 = true
        Processor.IFF2 = true

        ULATiming.advanceCycles(ULATiming.T_STATES_PER_FRAME)

        Processor.step()

        assertEquals(0x0038, Registers.specialPurposeRegisters.getPC())
        assertEquals(0x01.toByte(), Memory.memorySet.getMemoryCell(0xFFFCu))
        assertEquals(0x00.toByte(), Memory.memorySet.getMemoryCell(0xFFFDu))
        assertFalse(Processor.inHalt)
    }
}
