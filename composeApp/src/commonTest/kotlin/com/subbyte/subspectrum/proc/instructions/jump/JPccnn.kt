package com.subbyte.subspectrum.proc.instructions.jump

import com.subbyte.subspectrum.base.ConditionCode
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class JPccnnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstructionJPZNZ() {
        val word = (0xC2L shl 16) or (0x1234L) // C2 12 34: JP NZ, 1234h
        val instruction = JPccnn.decode(word, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0xC2.toByte(), instruction.bytes[0])
        assertEquals(0x12.toByte(), instruction.bytes[1])
        assertEquals(0x34.toByte(), instruction.bytes[2])

        val jpccnn = instruction as JPccnn
        assertEquals(ConditionCode.NZ, jpccnn.conditionCode)
        assertEquals(0x3412u, jpccnn.targetAddress)
    }

    @Test
    fun decodeInstructionJPZ() {
        val word = (0xCAL shl 16) or (0x5678L) // CA 56 78: JP Z, 5678h
        val instruction = JPccnn.decode(word, 0x1000u)

        val jpccnn = instruction as JPccnn
        assertEquals(ConditionCode.Z, jpccnn.conditionCode)
        assertEquals(0x7856u, jpccnn.targetAddress)
    }

    @Test
    fun decodeInstructionJPNC() {
        val word = (0xD2L shl 16) or (0x9ABCL) // D2 9A BC: JP NC, 9ABCh
        val instruction = JPccnn.decode(word, 0x1000u)

        val jpccnn = instruction as JPccnn
        assertEquals(ConditionCode.NC, jpccnn.conditionCode)
        assertEquals(0xBC9Au, jpccnn.targetAddress)
    }

    @Test
    fun decodeInstructionJPC() {
        val word = (0xDAL shl 16) or (0xDEF0L) // DA DE F0: JP C, DEF0h
        val instruction = JPccnn.decode(word, 0x1000u)

        val jpccnn = instruction as JPccnn
        assertEquals(ConditionCode.C, jpccnn.conditionCode)
        assertEquals(0xF0DEu, jpccnn.targetAddress)
    }

    @Test
    fun executeJumpWhenConditionTrue() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setZFlag(false) // NZ condition should be true

        val instruction = JPccnn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xC2.toByte(), 0x56.toByte(), 0x78.toByte())),
            conditionCode = ConditionCode.NZ,
            targetAddress = 0x5678u
        )

        instruction.execute()

        assertEquals<Short>(0x5678, Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeNoJumpWhenConditionFalse() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setZFlag(true) // NZ condition should be false

        val instruction = JPccnn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xC2.toByte(), 0x56.toByte(), 0x78.toByte())),
            conditionCode = ConditionCode.NZ,
            targetAddress = 0x5678u
        )

        instruction.execute()

        assertEquals<Short>(0x1000, Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeJumpZWhenZFlagSet() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setZFlag(true)

        val instruction = JPccnn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xCA.toByte(), 0x12.toByte(), 0x34.toByte())),
            conditionCode = ConditionCode.Z,
            targetAddress = 0x1234u
        )

        instruction.execute()

        assertEquals(0x1234.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeNoJumpZWhenZFlagClear() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setZFlag(false)

        val instruction = JPccnn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xCA.toByte(), 0x12.toByte(), 0x34.toByte())),
            conditionCode = ConditionCode.Z,
            targetAddress = 0x1234u
        )

        instruction.execute()

        assertEquals<Short>(0x1000, Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeJumpCWhenCFlagSet() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setCFlag(true)

        val instruction = JPccnn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xDA.toByte(), 0xAB.toByte(), 0xCD.toByte())),
            conditionCode = ConditionCode.C,
            targetAddress = 0xABCDu
        )

        instruction.execute()

        assertEquals(0xABCD.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeJumpNCWhenCFlagClear() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setCFlag(false)

        val instruction = JPccnn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xD2.toByte(), 0xEF.toByte(), 0x01.toByte())),
            conditionCode = ConditionCode.NC,
            targetAddress = 0xEF01u
        )

        instruction.execute()

        assertEquals(0xEF01.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeJumpPWhenSFlagClear() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setSFlag(false)

        val instruction = JPccnn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xF2.toByte(), 0x23.toByte(), 0x45.toByte())),
            conditionCode = ConditionCode.P,
            targetAddress = 0x2345u
        )

        instruction.execute()

        assertEquals(0x2345.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeJumpMWhenSFlagSet() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setSFlag(true)

        val instruction = JPccnn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xFA.toByte(), 0x67.toByte(), 0x89.toByte())),
            conditionCode = ConditionCode.M,
            targetAddress = 0x6789u
        )

        instruction.execute()

        assertEquals(0x6789.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeJumpPEWhenPVFlagSet() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setPVFlag(true)

        val instruction = JPccnn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xEA.toByte(), 0xBC.toByte(), 0xDE.toByte())),
            conditionCode = ConditionCode.PE,
            targetAddress = 0xBCDEu
        )

        instruction.execute()

        assertEquals(0xBCDE.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun executeJumpPOWhenPVFlagClear() {
        Registers.specialPurposeRegisters.setPC(0x1000)
        Registers.registerSet.setPVFlag(false)

        val instruction = JPccnn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xE2.toByte(), 0xF0.toByte(), 0x12.toByte())),
            conditionCode = ConditionCode.PO,
            targetAddress = 0xF012u
        )

        instruction.execute()

        assertEquals(0xF012.toShort(), Registers.specialPurposeRegisters.getPC())
    }

    @Test
    fun toStringFormat() {
        val instruction = JPccnn(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xC2.toByte(), 0x12.toByte(), 0x34.toByte())),
            conditionCode = ConditionCode.NZ,
            targetAddress = 0x1234u
        )

        assertEquals("JP NZ, 1234h", instruction.toString())
    }

    @Test
    fun toStringFormatZCondition() {
        val instruction = JPccnn(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xCA.toByte(), 0x56.toByte(), 0x78.toByte())),
            conditionCode = ConditionCode.Z,
            targetAddress = 0x5678u
        )

        assertEquals("JP Z, 5678h", instruction.toString())
    }
}
