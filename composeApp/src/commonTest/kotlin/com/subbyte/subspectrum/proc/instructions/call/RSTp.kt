package com.subbyte.subspectrum.proc.instructions.call

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RSTpTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstructionRST00() {
        val word = 0xC7L
        val instruction = RSTp.decode(word, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0xC7.toByte(), instruction.bytes[0])

        val rstp = instruction as RSTp
        assertEquals(0x00.toUByte(), rstp.restartAddress)
    }

    @Test
    fun decodeInstructionRST08() {
        val word = 0xCFL
        val instruction = RSTp.decode(word, 0x1000u)

        val rstp = instruction as RSTp
        assertEquals(0x08.toUByte(), rstp.restartAddress)
    }

    @Test
    fun decodeInstructionRST10() {
        val word = 0xD7L
        val instruction = RSTp.decode(word, 0x1000u)

        val rstp = instruction as RSTp
        assertEquals(0x10.toUByte(), rstp.restartAddress)
    }

    @Test
    fun decodeInstructionRST18() {
        val word = 0xDFL
        val instruction = RSTp.decode(word, 0x1000u)

        val rstp = instruction as RSTp
        assertEquals(0x18.toUByte(), rstp.restartAddress)
    }

    @Test
    fun decodeInstructionRST20() {
        val word = 0xE7L
        val instruction = RSTp.decode(word, 0x1000u)

        val rstp = instruction as RSTp
        assertEquals(0x20.toUByte(), rstp.restartAddress)
    }

    @Test
    fun decodeInstructionRST28() {
        val word = 0xEFL
        val instruction = RSTp.decode(word, 0x1000u)

        val rstp = instruction as RSTp
        assertEquals(0x28.toUByte(), rstp.restartAddress)
    }

    @Test
    fun decodeInstructionRST30() {
        val word = 0xF7L
        val instruction = RSTp.decode(word, 0x1000u)

        val rstp = instruction as RSTp
        assertEquals(0x30.toUByte(), rstp.restartAddress)
    }

    @Test
    fun decodeInstructionRST38() {
        val word = 0xFFL
        val instruction = RSTp.decode(word, 0x1000u)

        val rstp = instruction as RSTp
        assertEquals(0x38.toUByte(), rstp.restartAddress)
    }

    @Test
    fun executeRST00() {
        Registers.specialPurposeRegisters.setPC(0x15B3.toShort())
        Registers.specialPurposeRegisters.setSP(0xFFFE.toShort())

        val instruction = RSTp(
            address = 0x15B3u,
            bytes = byteArrayOf(0xC7.toByte()),
            restartAddress = 0x00u
        )

        instruction.execute()

        assertEquals(0x0000.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFC.toShort(), Registers.specialPurposeRegisters.getSP())
        assertEquals<Byte>(0xB3.toByte(), Memory.memorySet.getMemoryCell(0xFFFCu))
        assertEquals<Byte>(0x15.toByte(), Memory.memorySet.getMemoryCell(0xFFFDu))
    }

    @Test
    fun executeRST08() {
        Registers.specialPurposeRegisters.setPC(0x1234.toShort())
        Registers.specialPurposeRegisters.setSP(0x8000.toShort())

        val instruction = RSTp(
            address = 0x1234u,
            bytes = byteArrayOf(0xCF.toByte()),
            restartAddress = 0x08u
        )

        instruction.execute()

        assertEquals(0x0008.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0x7FFE.toShort(), Registers.specialPurposeRegisters.getSP())
        assertEquals<Byte>(0x34.toByte(), Memory.memorySet.getMemoryCell(0x7FFEu))
        assertEquals<Byte>(0x12.toByte(), Memory.memorySet.getMemoryCell(0x7FFFu))
    }

    @Test
    fun executeRST10() {
        Registers.specialPurposeRegisters.setPC(0xABCD.toShort())
        Registers.specialPurposeRegisters.setSP(0xC000.toShort())

        val instruction = RSTp(
            address = 0xABCDu,
            bytes = byteArrayOf(0xD7.toByte()),
            restartAddress = 0x10u
        )

        instruction.execute()

        assertEquals(0x0010.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xBFFE.toShort(), Registers.specialPurposeRegisters.getSP())
        assertEquals<Byte>(0xCD.toByte(), Memory.memorySet.getMemoryCell(0xBFFEu))
        assertEquals<Byte>(0xAB.toByte(), Memory.memorySet.getMemoryCell(0xBFFFu))
    }

    @Test
    fun executeRST18FromDocumentation() {
        Registers.specialPurposeRegisters.setPC(0x15B3.toShort())
        Registers.specialPurposeRegisters.setSP(0xFFFE.toShort())

        val instruction = RSTp(
            address = 0x15B3u,
            bytes = byteArrayOf(0xDF.toByte()),
            restartAddress = 0x18u
        )

        instruction.execute()

        assertEquals(0x0018.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFC.toShort(), Registers.specialPurposeRegisters.getSP())
        assertEquals<Byte>(0xB3.toByte(), Memory.memorySet.getMemoryCell(0xFFFCu))
        assertEquals<Byte>(0x15.toByte(), Memory.memorySet.getMemoryCell(0xFFFDu))
    }

    @Test
    fun executeRST20() {
        Registers.specialPurposeRegisters.setPC(0x5678.toShort())
        Registers.specialPurposeRegisters.setSP(0xD000.toShort())

        val instruction = RSTp(
            address = 0x5678u,
            bytes = byteArrayOf(0xE7.toByte()),
            restartAddress = 0x20u
        )

        instruction.execute()

        assertEquals(0x0020.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xCFFE.toShort(), Registers.specialPurposeRegisters.getSP())
        assertEquals<Byte>(0x78.toByte(), Memory.memorySet.getMemoryCell(0xCFFEu))
        assertEquals<Byte>(0x56.toByte(), Memory.memorySet.getMemoryCell(0xCFFFu))
    }

    @Test
    fun executeRST28() {
        Registers.specialPurposeRegisters.setPC(0x9ABC.toShort())
        Registers.specialPurposeRegisters.setSP(0xE000.toShort())

        val instruction = RSTp(
            address = 0x9ABCu,
            bytes = byteArrayOf(0xEF.toByte()),
            restartAddress = 0x28u
        )

        instruction.execute()

        assertEquals(0x0028.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xDFFE.toShort(), Registers.specialPurposeRegisters.getSP())
        assertEquals<Byte>(0xBC.toByte(), Memory.memorySet.getMemoryCell(0xDFFEu))
        assertEquals<Byte>(0x9A.toByte(), Memory.memorySet.getMemoryCell(0xDFFFu))
    }

    @Test
    fun executeRST30() {
        Registers.specialPurposeRegisters.setPC(0xDEF0.toShort())
        Registers.specialPurposeRegisters.setSP(0xF000.toShort())

        val instruction = RSTp(
            address = 0xDEF0u,
            bytes = byteArrayOf(0xF7.toByte()),
            restartAddress = 0x30u
        )

        instruction.execute()

        assertEquals(0x0030.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xEFFE.toShort(), Registers.specialPurposeRegisters.getSP())
        assertEquals<Byte>(0xF0.toByte(), Memory.memorySet.getMemoryCell(0xEFFEu))
        assertEquals<Byte>(0xDE.toByte(), Memory.memorySet.getMemoryCell(0xEFFFu))
    }

    @Test
    fun executeRST38() {
        Registers.specialPurposeRegisters.setPC(0xFEDC.toShort())
        Registers.specialPurposeRegisters.setSP(0xA000.toShort())

        val instruction = RSTp(
            address = 0xFEDCu,
            bytes = byteArrayOf(0xFF.toByte()),
            restartAddress = 0x38u
        )

        instruction.execute()

        assertEquals(0x0038.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0x9FFE.toShort(), Registers.specialPurposeRegisters.getSP())
        assertEquals<Byte>(0xDC.toByte(), Memory.memorySet.getMemoryCell(0x9FFEu))
        assertEquals<Byte>(0xFE.toByte(), Memory.memorySet.getMemoryCell(0x9FFFu))
    }

    @Test
    fun executeRSTAndReturn() {
        Registers.specialPurposeRegisters.setPC(0x2000.toShort())
        Registers.specialPurposeRegisters.setSP(0xFFFE.toShort())

        val rst = RSTp(
            address = 0x2000u,
            bytes = byteArrayOf(0xDF.toByte()),
            restartAddress = 0x18u
        )
        rst.execute()

        assertEquals(0x0018.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFC.toShort(), Registers.specialPurposeRegisters.getSP())

        val ret = RET(
            address = 0x0018u,
            bytes = byteArrayOf(0xC9.toByte())
        )
        ret.execute()

        assertEquals(0x2000.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFE.toShort(), Registers.specialPurposeRegisters.getSP())
    }

    @Test
    fun toStringFormat00() {
        val instruction = RSTp(
            address = 0x0000u,
            bytes = byteArrayOf(0xC7.toByte()),
            restartAddress = 0x00u
        )

        assertEquals("RST 00h", instruction.toString())
    }

    @Test
    fun toStringFormat18() {
        val instruction = RSTp(
            address = 0x0000u,
            bytes = byteArrayOf(0xDF.toByte()),
            restartAddress = 0x18u
        )

        assertEquals("RST 18h", instruction.toString())
    }

    @Test
    fun toStringFormat38() {
        val instruction = RSTp(
            address = 0x0000u,
            bytes = byteArrayOf(0xFF.toByte()),
            restartAddress = 0x38u
        )

        assertEquals("RST 38h", instruction.toString())
    }
}
