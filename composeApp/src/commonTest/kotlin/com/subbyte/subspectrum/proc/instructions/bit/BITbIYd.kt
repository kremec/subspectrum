package com.subbyte.subspectrum.proc.instructions.bit

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BITbIYdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        // FD CB 05 46: BIT 0, (IY + 5)
        // 0xFD = 11111101, 0xCB = 11001011, 0x05 = displacement, 0x46 = 01 000 110 (bit 0, register 6)
        val word = (0xFDL shl 24) or (0xCBL shl 16) or (0x05L shl 8) or 0x46L
        val instruction = BITbIYd.decode(word, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(4, instruction.bytes.size)
        assertEquals(0xFD.toByte(), instruction.bytes[0])
        assertEquals(0xCB.toByte(), instruction.bytes[1])
        assertEquals(0x05.toByte(), instruction.bytes[2])
        assertEquals(0x46.toByte(), instruction.bytes[3])

        val bitbIYd = instruction as BITbIYd
        assertEquals(0, bitbIYd.bit)
        assertEquals(5.toByte(), bitbIYd.displacement)
    }

    @Test
    fun decodeInstructionBit7() {
        // FD CB 0A 7E: BIT 7, (IY + 10)
        val word = (0xFDL shl 24) or (0xCBL shl 16) or (0x0AL shl 8) or 0x7EL
        val instruction = BITbIYd.decode(word, 0x1000u)

        val bitbIYd = instruction as BITbIYd
        assertEquals(7, bitbIYd.bit)
        assertEquals(10.toByte(), bitbIYd.displacement)
    }

    @Test
    fun executeBitTestMemoryBitSet() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x01.toByte()) // bit 0 set

        val instruction = BITbIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x05.toByte(), 0x46.toByte()),
            bit = 0,
            displacement = 5
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getZFlag()) // Z=0 because bit is set
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeBitTestMemoryBitClear() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0xFE.toByte()) // bit 0 clear

        val instruction = BITbIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x05.toByte(), 0x46.toByte()),
            bit = 0,
            displacement = 5
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getZFlag()) // Z=1 because bit is clear
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeBitTestMemoryBit7Set() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x200Au, 0x80.toByte()) // bit 7 set

        val instruction = BITbIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x0A.toByte(), 0x7E.toByte()),
            bit = 7,
            displacement = 10
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getZFlag()) // Z=0 because bit 7 is set
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeBitTestMemoryBit7Clear() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x200Au, 0x7F.toByte()) // bit 7 clear

        val instruction = BITbIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x0A.toByte(), 0x7E.toByte()),
            bit = 7,
            displacement = 10
        )

        instruction.execute()

        assertTrue(Registers.registerSet.getZFlag()) // Z=1 because bit 7 is clear
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeBitTestDifferentBits() {
        Registers.specialPurposeRegisters.setIY(0x1000.toShort())

        for (bit in 0..7) {
            Memory.memorySet.setMemoryCell(0x1002u, (1 shl bit).toByte())

            val instruction = BITbIYd(
                address = 0x1000u,
                bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x02.toByte(), (0x40 or (bit shl 3)).toByte()),
                bit = bit,
                displacement = 2
            )

            instruction.execute()

            assertFalse(Registers.registerSet.getZFlag(), "Bit $bit should be set")
            assertTrue(Registers.registerSet.getHFlag())
            assertFalse(Registers.registerSet.getNFlag())
        }
    }

    @Test
    fun executeBitTestNegativeDisplacement() {
        Registers.specialPurposeRegisters.setIY(0x2010.toShort())
        Memory.memorySet.setMemoryCell(0x200Bu, 0x01.toByte()) // bit 0 set

        val instruction = BITbIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0xFB.toByte(), 0x46.toByte()),
            bit = 0,
            displacement = -5
        )

        instruction.execute()

        assertFalse(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }



    @Test
    fun toStringFormat() {
        val instruction = BITbIYd(
            address = 0x0000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x05.toByte(), 0x46.toByte()),
            bit = 0,
            displacement = 5
        )

        assertEquals("BIT 0, (IY + 5)", instruction.toString())
    }

    @Test
    fun toStringFormatNegativeDisplacement() {
        val instruction = BITbIYd(
            address = 0x0000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0xFB.toByte(), 0x46.toByte()),
            bit = 3,
            displacement = -5
        )

        assertEquals("BIT 3, (IY + -5)", instruction.toString())
    }
}
