package com.subbyte.subspectrum.proc.instructions.bit

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RESbIYdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val word = (0xFDL shl 24) or (0xCBL shl 16) or (0x05L shl 8) or 0x86L
        val instruction = RESbIYd.decode(word, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(4, instruction.bytes.size)
        assertEquals(0xFD.toByte(), instruction.bytes[0])
        assertEquals(0xCB.toByte(), instruction.bytes[1])
        assertEquals(0x05.toByte(), instruction.bytes[2])
        assertEquals(0x86.toByte(), instruction.bytes[3])

        val resbIYd = instruction as RESbIYd
        assertEquals(0, resbIYd.bit)
        assertEquals(5.toByte(), resbIYd.displacement)
    }

    @Test
    fun decodeInstructionBit7() {
        val word = (0xFDL shl 24) or (0xCBL shl 16) or (0x0AL shl 8) or 0xBEL
        val instruction = RESbIYd.decode(word, 0x1000u)

        val resbIYd = instruction as RESbIYd
        assertEquals(7, resbIYd.bit)
        assertEquals(10.toByte(), resbIYd.displacement)
    }

    @Test
    fun executeResetBit() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        val targetAddress = 0x2005u.toUShort()
        Memory.memorySet.setMemoryCell(targetAddress, 0x01.toByte()) // bit 0 set

        val instruction = RESbIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x05.toByte(), 0x86.toByte()),
            bit = 0,
            displacement = 5
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Memory.memorySet.getMemoryCell(targetAddress)) // bit 0 cleared
    }

    @Test
    fun executeResetBitAlreadyClear() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        val targetAddress = 0x2005u.toUShort()
        Memory.memorySet.setMemoryCell(targetAddress, 0x00.toByte()) // bit 0 already clear

        val instruction = RESbIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x05.toByte(), 0x86.toByte()),
            bit = 0,
            displacement = 5
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Memory.memorySet.getMemoryCell(targetAddress)) // bit 0 still clear
    }

    @Test
    fun executeResetBit7() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        val targetAddress = 0x200Au.toUShort()
        Memory.memorySet.setMemoryCell(targetAddress, 0xFF.toByte()) // bit 7 set

        val instruction = RESbIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x0A.toByte(), 0xBE.toByte()),
            bit = 7,
            displacement = 10
        )

        instruction.execute()

        assertEquals(0x7F.toByte(), Memory.memorySet.getMemoryCell(targetAddress)) // bit 7 cleared
    }

    @Test
    fun executeResetBitNoFlagsAffected() {
        Registers.specialPurposeRegisters.setIY(0x2000.toShort())
        val targetAddress = 0x2005u.toUShort()
        Memory.memorySet.setMemoryCell(targetAddress, 0x01.toByte())
        // Set some flags
        Registers.registerSet.setZFlag(true)
        Registers.registerSet.setCFlag(true)
        Registers.registerSet.setNFlag(true)

        val instruction = RESbIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x05.toByte(), 0x86.toByte()),
            bit = 0,
            displacement = 5
        )

        instruction.execute()

        // Flags should remain unchanged
        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeResetDifferentBits() {
        Registers.specialPurposeRegisters.setIY(0x1000.toShort())

        for (bit in 0..7) {
            val targetAddress = 0x1002u.toUShort()
            Memory.memorySet.setMemoryCell(targetAddress, 0xFF.toByte())

            val instruction = RESbIYd(
                address = 0x1000u,
                bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x02.toByte(), (0x86 or (bit shl 3)).toByte()),
                bit = bit,
                displacement = 2
            )

            instruction.execute()

            val expectedValue = (0xFF and (1 shl bit).inv()).toByte()
            assertEquals(expectedValue, Memory.memorySet.getMemoryCell(targetAddress))
        }
    }

    @Test
    fun executeResetNegativeDisplacement() {
        Registers.specialPurposeRegisters.setIY(0x2010.toShort())
        val targetAddress = 0x200Bu.toUShort() // IY + (-5) = 0x2010 - 5 = 0x200B
        Memory.memorySet.setMemoryCell(targetAddress, 0x01.toByte()) // bit 0 set

        val instruction = RESbIYd(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0xFB.toByte(), 0x86.toByte()),
            bit = 0,
            displacement = -5
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Memory.memorySet.getMemoryCell(targetAddress)) // bit 0 cleared
    }

    @Test
    fun toStringFormat() {
        val instruction = RESbIYd(
            address = 0x0000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0x05.toByte(), 0x86.toByte()),
            bit = 0,
            displacement = 5
        )

        assertEquals("RES 0, (IY + 5)", instruction.toString())
    }

    @Test
    fun toStringFormatNegativeDisplacement() {
        val instruction = RESbIYd(
            address = 0x0000u,
            bytes = byteArrayOf(0xFD.toByte(), 0xCB.toByte(), 0xFB.toByte(), 0x86.toByte()),
            bit = 3,
            displacement = -5
        )

        assertEquals("RES 3, (IY + -5)", instruction.toString())
    }
}
