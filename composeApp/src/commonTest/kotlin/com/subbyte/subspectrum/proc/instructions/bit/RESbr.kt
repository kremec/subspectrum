package com.subbyte.subspectrum.proc.instructions.bit

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterCode
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RESbrTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = RESbr.decode(0xCB80L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xCB.toByte(), instruction.bytes[0])
        assertEquals(0x80.toByte(), instruction.bytes[1])

        val resbr = instruction as RESbr
        assertEquals(0, resbr.bit)
        assertEquals(RegisterCode.B, resbr.sourceRegister)
    }

    @Test
    fun decodeInstructionBit7() {
        val instruction = RESbr.decode(0xCBBFL, 0x1000u)

        val resbr = instruction as RESbr
        assertEquals(7, resbr.bit)
        assertEquals(RegisterCode.A, resbr.sourceRegister)
    }

    @Test
    fun executeResetBit() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x01.toByte()) // bit 0 set

        val instruction = RESbr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x80.toByte()),
            bit = 0,
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getRegister(RegisterCode.B)) // bit 0 cleared
    }

    @Test
    fun executeResetBitAlreadyClear() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x00.toByte()) // bit 0 already clear

        val instruction = RESbr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x80.toByte()),
            bit = 0,
            sourceRegister = RegisterCode.B
        )

        instruction.execute()

        assertEquals(0x00.toByte(), Registers.registerSet.getRegister(RegisterCode.B)) // bit 0 still clear
    }

    @Test
    fun executeResetBit7() {
        Registers.registerSet.setRegister(RegisterCode.A, 0xFF.toByte()) // bit 7 set

        val instruction = RESbr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0xBF.toByte()),
            bit = 7,
            sourceRegister = RegisterCode.A
        )

        instruction.execute()

        assertEquals(0x7F.toByte(), Registers.registerSet.getRegister(RegisterCode.A)) // bit 7 cleared
    }

    @Test
    fun executeResetBitNoFlagsAffected() {
        Registers.registerSet.setRegister(RegisterCode.B, 0x01.toByte())
        // Set some flags
        Registers.registerSet.setZFlag(true)
        Registers.registerSet.setCFlag(true)
        Registers.registerSet.setNFlag(true)

        val instruction = RESbr(
            address = 0x1000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x80.toByte()),
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
    fun executeResetDifferentBits() {
        for (bit in 0..7) {
            Registers.registerSet.setRegister(RegisterCode.C, 0xFF.toByte())

            val instruction = RESbr(
                address = 0x1000u,
                bytes = byteArrayOf(0xCB.toByte(), (0x80 or (bit shl 3)).toByte()),
                bit = bit,
                sourceRegister = RegisterCode.C
            )

            instruction.execute()

            val expectedValue = (0xFF and (1 shl bit).inv()).toByte()
            assertEquals(expectedValue, Registers.registerSet.getRegister(RegisterCode.C))
        }
    }

    @Test
    fun toStringFormat() {
        val instruction = RESbr(
            address = 0x0000u,
            bytes = byteArrayOf(0xCB.toByte(), 0x80.toByte()),
            bit = 0,
            sourceRegister = RegisterCode.B
        )

        assertEquals("RES 0, B", instruction.toString())
    }
}
