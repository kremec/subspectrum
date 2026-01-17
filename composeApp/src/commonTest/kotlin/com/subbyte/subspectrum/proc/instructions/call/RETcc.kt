package com.subbyte.subspectrum.proc.instructions.call

import com.subbyte.subspectrum.base.ConditionCode
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RETccTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstructionRETNZ() {
        val word = 0xC0L
        val instruction = RETcc.decode(word, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0xC0.toByte(), instruction.bytes[0])

        val retcc = instruction as RETcc
        assertEquals(ConditionCode.NZ, retcc.condition)
    }

    @Test
    fun decodeInstructionRETZ() {
        val word = 0xC8L
        val instruction = RETcc.decode(word, 0x1000u)

        val retcc = instruction as RETcc
        assertEquals(ConditionCode.Z, retcc.condition)
    }

    @Test
    fun decodeInstructionRETC() {
        val word = 0xD8L
        val instruction = RETcc.decode(word, 0x1000u)

        val retcc = instruction as RETcc
        assertEquals(ConditionCode.C, retcc.condition)
    }

    @Test
    fun decodeInstructionRETNC() {
        val word = 0xD0L
        val instruction = RETcc.decode(word, 0x1000u)

        val retcc = instruction as RETcc
        assertEquals(ConditionCode.NC, retcc.condition)
    }

    @Test
    fun executeReturnWhenConditionTrue() {
        Registers.specialPurposeRegisters.setSP(0xFFFC.toShort())
        Memory.memorySet.setMemoryCell(0xFFFCu, 0x34.toByte())
        Memory.memorySet.setMemoryCell(0xFFFDu, 0x12.toByte())
        Registers.registerSet.setZFlag(false)

        val instruction = RETcc(
            address = 0x5000u,
            bytes = byteArrayOf(0xC0.toByte()),
            condition = ConditionCode.NZ
        )

        instruction.execute()

        assertEquals(0x1234.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFE.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeNoReturnWhenConditionFalse() {
        Registers.specialPurposeRegisters.setPC(0x5000.toShort())
        Registers.specialPurposeRegisters.setSP(0xFFFC.toShort())
        Memory.memorySet.setMemoryCell(0xFFFCu, 0x34.toByte())
        Memory.memorySet.setMemoryCell(0xFFFDu, 0x12.toByte())
        Registers.registerSet.setZFlag(true)

        val instruction = RETcc(
            address = 0x5000u,
            bytes = byteArrayOf(0xC0.toByte()),
            condition = ConditionCode.NZ
        )

        instruction.execute()

        assertEquals(0x5000.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFC.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeReturnZWhenZFlagSet() {
        Registers.specialPurposeRegisters.setSP(0x8000.toShort())
        Memory.memorySet.setMemoryCell(0x8000u, 0x00.toByte())
        Memory.memorySet.setMemoryCell(0x8001u, 0x20.toByte())
        Registers.registerSet.setZFlag(true)

        val instruction = RETcc(
            address = 0x3000u,
            bytes = byteArrayOf(0xC8.toByte()),
            condition = ConditionCode.Z
        )

        instruction.execute()

        assertEquals(0x2000.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0x8002.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeNoReturnZWhenZFlagClear() {
        Registers.specialPurposeRegisters.setPC(0x3000.toShort())
        Registers.specialPurposeRegisters.setSP(0x8000.toShort())
        Memory.memorySet.setMemoryCell(0x8000u, 0x00.toByte())
        Memory.memorySet.setMemoryCell(0x8001u, 0x20.toByte())
        Registers.registerSet.setZFlag(false)

        val instruction = RETcc(
            address = 0x3000u,
            bytes = byteArrayOf(0xC8.toByte()),
            condition = ConditionCode.Z
        )

        instruction.execute()

        assertEquals(0x3000.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0x8000.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeReturnCWhenCFlagSet() {
        Registers.specialPurposeRegisters.setSP(0xC000.toShort())
        Memory.memorySet.setMemoryCell(0xC000u, 0xCD.toByte())
        Memory.memorySet.setMemoryCell(0xC001u, 0xAB.toByte())
        Registers.registerSet.setCFlag(true)

        val instruction = RETcc(
            address = 0x4000u,
            bytes = byteArrayOf(0xD8.toByte()),
            condition = ConditionCode.C
        )

        instruction.execute()

        assertEquals(0xABCD.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xC002.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeReturnNCWhenCFlagClear() {
        Registers.specialPurposeRegisters.setSP(0xD000.toShort())
        Memory.memorySet.setMemoryCell(0xD000u, 0x01.toByte())
        Memory.memorySet.setMemoryCell(0xD001u, 0xEF.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = RETcc(
            address = 0x5000u,
            bytes = byteArrayOf(0xD0.toByte()),
            condition = ConditionCode.NC
        )

        instruction.execute()

        assertEquals(0xEF01.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xD002.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeReturnPWhenSFlagClear() {
        Registers.specialPurposeRegisters.setSP(0xE000.toShort())
        Memory.memorySet.setMemoryCell(0xE000u, 0x45.toByte())
        Memory.memorySet.setMemoryCell(0xE001u, 0x23.toByte())
        Registers.registerSet.setSFlag(false)

        val instruction = RETcc(
            address = 0x6000u,
            bytes = byteArrayOf(0xF0.toByte()),
            condition = ConditionCode.P
        )

        instruction.execute()

        assertEquals(0x2345.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xE002.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeReturnMWhenSFlagSet() {
        Registers.specialPurposeRegisters.setSP(0xF000.toShort())
        Memory.memorySet.setMemoryCell(0xF000u, 0x89.toByte())
        Memory.memorySet.setMemoryCell(0xF001u, 0x67.toByte())
        Registers.registerSet.setSFlag(true)

        val instruction = RETcc(
            address = 0x7000u,
            bytes = byteArrayOf(0xF8.toByte()),
            condition = ConditionCode.M
        )

        instruction.execute()

        assertEquals(0x6789.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xF002.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeReturnPEWhenPVFlagSet() {
        Registers.specialPurposeRegisters.setSP(0xA000.toShort())
        Memory.memorySet.setMemoryCell(0xA000u, 0xDE.toByte())
        Memory.memorySet.setMemoryCell(0xA001u, 0xBC.toByte())
        Registers.registerSet.setPVFlag(true)

        val instruction = RETcc(
            address = 0x8000u,
            bytes = byteArrayOf(0xE8.toByte()),
            condition = ConditionCode.PE
        )

        instruction.execute()

        assertEquals(0xBCDE.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xA002.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeReturnPOWhenPVFlagClear() {
        Registers.specialPurposeRegisters.setSP(0xB000.toShort())
        Memory.memorySet.setMemoryCell(0xB000u, 0x12.toByte())
        Memory.memorySet.setMemoryCell(0xB001u, 0xF0.toByte())
        Registers.registerSet.setPVFlag(false)

        val instruction = RETcc(
            address = 0x9000u,
            bytes = byteArrayOf(0xE0.toByte()),
            condition = ConditionCode.PO
        )

        instruction.execute()

        assertEquals(0xF012.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xB002.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeCallAndConditionalReturnSequence() {
        Registers.specialPurposeRegisters.setPC(0x1003.toShort())
        Registers.specialPurposeRegisters.setSP(0xFFFE.toShort())
        Registers.registerSet.setZFlag(false)

        val callInstruction = CALLnn(
            address = 0x1000u,
            bytes = byteArrayOf(0xCD.toByte(), 0x00.toByte(), 0x20.toByte()),
            targetAddress = 0x2000u
        )
        callInstruction.execute()

        assertEquals(0x2000.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFC.toShort(), Registers.specialPurposeRegisters.getSP())

        val retInstruction = RETcc(
            address = 0x2000u,
            bytes = byteArrayOf(0xC0.toByte()),
            condition = ConditionCode.NZ
        )
        retInstruction.execute()

        assertEquals(0x1003.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFE.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun executeCallAndConditionalReturnNotTaken() {
        Registers.specialPurposeRegisters.setPC(0x1003.toShort())
        Registers.specialPurposeRegisters.setSP(0xFFFE.toShort())
        Registers.registerSet.setZFlag(true)

        val callInstruction = CALLnn(
            address = 0x1000u,
            bytes = byteArrayOf(0xCD.toByte(), 0x00.toByte(), 0x20.toByte()),
            targetAddress = 0x2000u
        )
        callInstruction.execute()

        assertEquals(0x2000.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFC.toShort(), Registers.specialPurposeRegisters.getSP())

        val retInstruction = RETcc(
            address = 0x2000u,
            bytes = byteArrayOf(0xC0.toByte()),
            condition = ConditionCode.NZ
        )
        retInstruction.execute()

        assertEquals(0x2000.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFC.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun toStringFormat() {
        val instruction = RETcc(
            address = 0x0000u,
            bytes = byteArrayOf(0xC0.toByte()),
            condition = ConditionCode.NZ
        )

        assertEquals("RET NZ", instruction.toString())
    }

    @Test
    fun toStringFormatZCondition() {
        val instruction = RETcc(
            address = 0x0000u,
            bytes = byteArrayOf(0xC8.toByte()),
            condition = ConditionCode.Z
        )

        assertEquals("RET Z", instruction.toString())
    }
}
