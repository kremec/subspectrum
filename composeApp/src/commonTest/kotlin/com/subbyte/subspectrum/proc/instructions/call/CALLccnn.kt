package com.subbyte.subspectrum.proc.instructions.call

import com.subbyte.subspectrum.base.ConditionCode
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CALLccnnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstructionCALLNZ() {
        val word = (0xC4L shl 16) or (0x3412L) // C4 34 12: CALL NZ, 1234h
        val instruction = CALLccnn.decode(word, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0xC4.toByte(), instruction.bytes[0])
        assertEquals(0x34.toByte(), instruction.bytes[1])
        assertEquals(0x12.toByte(), instruction.bytes[2])

        val callccnn = instruction as CALLccnn
        assertEquals(ConditionCode.NZ, callccnn.condition)
        assertEquals(0x1234u, callccnn.targetAddress)
    }

    @Test
    fun decodeInstructionCALLZ() {
        val word = (0xCCL shl 16) or (0x7856L) // CC 78 56: CALL Z, 5678h
        val instruction = CALLccnn.decode(word, 0x1000u)

        val callccnn = instruction as CALLccnn
        assertEquals(ConditionCode.Z, callccnn.condition)
        assertEquals(0x5678u, callccnn.targetAddress)
    }

    @Test
    fun decodeInstructionCALLNC() {
        val word = (0xD4L shl 16) or (0xBC9AL) // D4 BC 9A: CALL NC, 9ABCh
        val instruction = CALLccnn.decode(word, 0x1000u)

        val callccnn = instruction as CALLccnn
        assertEquals(ConditionCode.NC, callccnn.condition)
        assertEquals(0x9ABCu, callccnn.targetAddress)
    }

    @Test
    fun decodeInstructionCALLC() {
        val word = (0xDCL shl 16) or (0xF0DEL) // DC F0 DE: CALL C, DEF0h
        val instruction = CALLccnn.decode(word, 0x1000u)

        val callccnn = instruction as CALLccnn
        assertEquals(ConditionCode.C, callccnn.condition)
        assertEquals(0xDEF0u, callccnn.targetAddress)
    }

    @Test
    fun executeCallWhenConditionTrue() {
        Registers.specialPurposeRegisters.setPC(0x1003.toShort()) // PC after the 3-byte CALL instruction
        Registers.specialPurposeRegisters.setSP(0xFFFE.toShort())
        Registers.registerSet.setZFlag(false) // NZ condition should be true

        val instruction = CALLccnn(
            address = 0x1000u,
            bytes = byteArrayOf(0xC4.toByte(), 0x56.toByte(), 0x78.toByte()),
            condition = ConditionCode.NZ,
            targetAddress = 0x5678u
        )

        instruction.execute()

        // PC should jump to target address
        assertEquals(0x5678, Registers.specialPurposeRegisters.getPC())
        
        // SP should be decremented by 2
        assertEquals(0xFFFC.toShort(), Registers.specialPurposeRegisters.getSP())
        
        // Return address (0x1003) should be pushed onto stack
        assertEquals<Byte>(0x03.toByte(), Memory.memorySet.getMemoryCell(0xFFFCu))
        assertEquals<Byte>(0x10.toByte(), Memory.memorySet.getMemoryCell(0xFFFDu))
    }

    @Test
    fun executeNoCallWhenConditionFalse() {
        Registers.specialPurposeRegisters.setPC(0x1003.toShort())
        Registers.specialPurposeRegisters.setSP(0xFFFE.toShort())
        Registers.registerSet.setZFlag(true) // NZ condition should be false

        val instruction = CALLccnn(
            address = 0x1000u,
            bytes = byteArrayOf(0xC4.toByte(), 0x56.toByte(), 0x78.toByte()),
            condition = ConditionCode.NZ,
            targetAddress = 0x5678u
        )

        instruction.execute()

        // PC should NOT change
        assertEquals<Short>(0x1003, Registers.specialPurposeRegisters.getPC())
        
        // SP should NOT change
        assertEquals(0xFFFE.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeCallZWhenZFlagSet() {
        Registers.specialPurposeRegisters.setPC(0x2003.toShort())
        Registers.specialPurposeRegisters.setSP(0x8000.toShort())
        Registers.registerSet.setZFlag(true)

        val instruction = CALLccnn(
            address = 0x2000u,
            bytes = byteArrayOf(0xCC.toByte(), 0x34.toByte(), 0x12.toByte()),
            condition = ConditionCode.Z,
            targetAddress = 0x1234u
        )

        instruction.execute()

        assertEquals(0x1234.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0x7FFE.toShort(), Registers.specialPurposeRegisters.getSP())
        assertEquals<Byte>(0x03.toByte(), Memory.memorySet.getMemoryCell(0x7FFEu))
        assertEquals<Byte>(0x20.toByte(), Memory.memorySet.getMemoryCell(0x7FFFu))
    }

    @Test
    fun executeNoCallZWhenZFlagClear() {
        Registers.specialPurposeRegisters.setPC(0x2003.toShort())
        Registers.specialPurposeRegisters.setSP(0x8000.toShort())
        Registers.registerSet.setZFlag(false)

        val instruction = CALLccnn(
            address = 0x2000u,
            bytes = byteArrayOf(0xCC.toByte(), 0x34.toByte(), 0x12.toByte()),
            condition = ConditionCode.Z,
            targetAddress = 0x1234u
        )

        instruction.execute()

        assertEquals<Short>(0x2003, Registers.specialPurposeRegisters.getPC())
        assertEquals(0x8000.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeCallCWhenCFlagSet() {
        Registers.specialPurposeRegisters.setPC(0x3003.toShort())
        Registers.specialPurposeRegisters.setSP(0xC000.toShort())
        Registers.registerSet.setCFlag(true)

        val instruction = CALLccnn(
            address = 0x3000u,
            bytes = byteArrayOf(0xDC.toByte(), 0xCD.toByte(), 0xAB.toByte()),
            condition = ConditionCode.C,
            targetAddress = 0xABCDu
        )

        instruction.execute()

        assertEquals(0xABCD.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xBFFE.toShort(), Registers.specialPurposeRegisters.getSP())
        assertEquals<Byte>(0x03.toByte(), Memory.memorySet.getMemoryCell(0xBFFEu))
        assertEquals<Byte>(0x30.toByte(), Memory.memorySet.getMemoryCell(0xBFFFu))
    }

    @Test
    fun executeCallNCWhenCFlagClear() {
        Registers.specialPurposeRegisters.setPC(0x4003.toShort())
        Registers.specialPurposeRegisters.setSP(0xD000.toShort())
        Registers.registerSet.setCFlag(false)

        val instruction = CALLccnn(
            address = 0x4000u,
            bytes = byteArrayOf(0xD4.toByte(), 0x01.toByte(), 0xEF.toByte()),
            condition = ConditionCode.NC,
            targetAddress = 0xEF01u
        )

        instruction.execute()

        assertEquals(0xEF01.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xCFFE.toShort(), Registers.specialPurposeRegisters.getSP())
        assertEquals<Byte>(0x03.toByte(), Memory.memorySet.getMemoryCell(0xCFFEu))
        assertEquals<Byte>(0x40.toByte(), Memory.memorySet.getMemoryCell(0xCFFFu))
    }

    @Test
    fun executeCallPWhenSFlagClear() {
        Registers.specialPurposeRegisters.setPC(0x5003.toShort())
        Registers.specialPurposeRegisters.setSP(0xE000.toShort())
        Registers.registerSet.setSFlag(false)

        val instruction = CALLccnn(
            address = 0x5000u,
            bytes = byteArrayOf(0xF4.toByte(), 0x45.toByte(), 0x23.toByte()),
            condition = ConditionCode.P,
            targetAddress = 0x2345u
        )

        instruction.execute()

        assertEquals(0x2345.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xDFFE.toShort(), Registers.specialPurposeRegisters.getSP())
        assertEquals<Byte>(0x03.toByte(), Memory.memorySet.getMemoryCell(0xDFFEu))
        assertEquals<Byte>(0x50.toByte(), Memory.memorySet.getMemoryCell(0xDFFFu))
    }

    @Test
    fun executeCallMWhenSFlagSet() {
        Registers.specialPurposeRegisters.setPC(0x6003.toShort())
        Registers.specialPurposeRegisters.setSP(0xF000.toShort())
        Registers.registerSet.setSFlag(true)

        val instruction = CALLccnn(
            address = 0x6000u,
            bytes = byteArrayOf(0xFC.toByte(), 0x89.toByte(), 0x67.toByte()),
            condition = ConditionCode.M,
            targetAddress = 0x6789u
        )

        instruction.execute()

        assertEquals(0x6789.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xEFFE.toShort(), Registers.specialPurposeRegisters.getSP())
        assertEquals<Byte>(0x03.toByte(), Memory.memorySet.getMemoryCell(0xEFFEu))
        assertEquals<Byte>(0x60.toByte(), Memory.memorySet.getMemoryCell(0xEFFFu))
    }

    @Test
    fun executeCallPEWhenPVFlagSet() {
        Registers.specialPurposeRegisters.setPC(0x7003.toShort())
        Registers.specialPurposeRegisters.setSP(0xA000.toShort())
        Registers.registerSet.setPVFlag(true)

        val instruction = CALLccnn(
            address = 0x7000u,
            bytes = byteArrayOf(0xEC.toByte(), 0xDE.toByte(), 0xBC.toByte()),
            condition = ConditionCode.PE,
            targetAddress = 0xBCDEu
        )

        instruction.execute()

        assertEquals(0xBCDE.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0x9FFE.toShort(), Registers.specialPurposeRegisters.getSP())
        assertEquals<Byte>(0x03.toByte(), Memory.memorySet.getMemoryCell(0x9FFEu))
        assertEquals<Byte>(0x70.toByte(), Memory.memorySet.getMemoryCell(0x9FFFu))
    }

    @Test
    fun executeCallPOWhenPVFlagClear() {
        Registers.specialPurposeRegisters.setPC(0x8003.toShort())
        Registers.specialPurposeRegisters.setSP(0xB000.toShort())
        Registers.registerSet.setPVFlag(false)

        val instruction = CALLccnn(
            address = 0x8000u,
            bytes = byteArrayOf(0xE4.toByte(), 0x12.toByte(), 0xF0.toByte()),
            condition = ConditionCode.PO,
            targetAddress = 0xF012u
        )

        instruction.execute()

        assertEquals(0xF012.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xAFFE.toShort(), Registers.specialPurposeRegisters.getSP())
        assertEquals<Byte>(0x03.toByte(), Memory.memorySet.getMemoryCell(0xAFFEu))
        assertEquals<Byte>(0x80.toByte(), Memory.memorySet.getMemoryCell(0xAFFFu))
    }

    @Test
    fun toStringFormat() {
        val instruction = CALLccnn(
            address = 0x0000u,
            bytes = byteArrayOf(0xC4.toByte(), 0x34.toByte(), 0x12.toByte()),
            condition = ConditionCode.NZ,
            targetAddress = 0x1234u
        )

        assertEquals("CALL NZ, 1234h", instruction.toString())
    }

    @Test
    fun toStringFormatZCondition() {
        val instruction = CALLccnn(
            address = 0x0000u,
            bytes = byteArrayOf(0xCC.toByte(), 0x78.toByte(), 0x56.toByte()),
            condition = ConditionCode.Z,
            targetAddress = 0x5678u
        )

        assertEquals("CALL Z, 5678h", instruction.toString())
    }
}
