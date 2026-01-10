package com.subbyte.subspectrum.proc.instructions.arith8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SBCAIXdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = SBCAIXd.decode(0xDD9E05L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0x9E.toByte(), instruction.bytes[1])
        assertEquals(0x05.toByte(), instruction.bytes[2])

        val sbcAIXd = instruction as SBCAIXd
        assertEquals(0x05.toByte(), sbcAIXd.displacement)
    }

    @Test
    fun executeSBCWithIXOffset() {
        Registers.registerSet.setA(0x30.toByte())
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2005u, 0x10.toByte())
        Registers.registerSet.setCFlag(false)

        val instruction = SBCAIXd(
            address = 0x1000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x9E.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        instruction.execute()

        assertEquals(0x20.toByte(), Registers.registerSet.getA())
        assertTrue(Registers.registerSet.getNFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = SBCAIXd(
            address = 0x0000u,
            bytes = byteArrayOf(0xDD.toByte(), 0x9E.toByte(), 0x05.toByte()),
            displacement = 0x05.toByte()
        )

        assertEquals("SBC A, (IX + 5)", instruction.toString())
    }
}
