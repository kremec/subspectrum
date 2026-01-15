package com.subbyte.subspectrum.proc.instructions.bit

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SETbrTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = SETbr.decode(0xCBC0L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xCB.toByte(), instruction.bytes[0])
        assertEquals(0xC0.toByte(), instruction.bytes[1])

        val setbr = instruction as SETbr
        assertEquals(0, setbr.bit)
        assertEquals(RegisterCode.B, setbr.sourceRegister)
    }

    @Test
    fun decodeInstructionBit7() {
        val instruction = SETbr.decode(0xCBFFL, 0x1000u)

        val setbr = instruction as SETbr
        assertEquals(7, setbr.bit)
        assertEquals(RegisterCode.A, setbr.sourceRegister)
    }

    @Test
    fun executeSetBit() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x00.toByte()) // bit 0 clear

        val instruction = SETbr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0xC0.toByte()),
            bit = 0,
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x01.toByte(), Registers.registerSet.getRegister(RegisterCode.B)) // bit 0 set
    }

    @Test
    fun executeSetBitAlreadySet() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x01.toByte()) // bit 0 already set

        val instruction = SETbr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0xC0.toByte()),
            bit = 0,
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x01.toByte(), Registers.registerSet.getRegister(RegisterCode.B)) // bit 0 still set
    }

    @Test
    fun executeSetBit7() {
        Registers.registerSet.setRegister(RegisterCode.A, 0x7F.toByte()) // bit 7 clear

        val instruction = SETbr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0xFF.toByte()),
            bit = 7,
            sourceRegister = RegisterCode.A
        )

        instruction.execute()

        assertEquals(0xFF.toByte(), Registers.registerSet.getRegister(RegisterCode.A)) // bit 7 set
    }

    @Test
    fun executeSetBitNoFlagsAffected() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x00.toByte())
        // Set some flags
        Registers.registerSet.setZFlag(true)
        Registers.registerSet.setCFlag(true)
        Registers.registerSet.setNFlag(true)

        val instruction = SETbr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0xC0.toByte()),
            bit = 0,
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        // Flags should remain unchanged
        assertTrue(Registers.registerSet.getZFlag())
        assertTrue(Registers.registerSet.getCFlag())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeSetDifferentBits() {
        for (bit in 0..7) {
            Registers.registerSet.setRegister(RegisterCode.C, 0x00.toByte())

            val instruction = SETbr(
                address = 0x1000u,
                bytes = byteArrayOf(0xCB.toByte(), (0xC0 or (bit shl 3)).toByte()),
                bit = bit,
                sourceRegister = RegisterCode.C
            )

            instruction.execute()

            val expectedValue = (1 shl bit).toByte()
            assertEquals(expectedValue, Registers.registerSet.getRegister(RegisterCode.C))
        }
    }

    @Test
    fun toStringFormat() {
        val instruction = SETbr(
            address = 0x0000u,
            bytes = byteArrayOf(0xCB.toByte(), 0xC0.toByte()),
            bit = 0,
            sourceRegister = RegisterCode.B
        )

        assertEquals("SET 0, B", instruction.toString())
    }
}
