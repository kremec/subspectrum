package com.subbyte.subspectrum.proc.instructions.undocumented.cb

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.instructions.Instructions
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SLLIXdTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = SLLIXd.decode(0xDDCB0136L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(4, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0xCB.toByte(), instruction.bytes[1])
        assertEquals(0x01.toByte(), instruction.bytes[2])
        assertEquals(0x36.toByte(), instruction.bytes[3])

        val sllIXd = instruction as SLLIXd
        assertEquals(0x01.toByte(), sllIXd.displacement)
        assertEquals(null, sllIXd.destinationRegister)
    }

    @Test
    fun decodeThroughInstructionsTable() {
        Memory.memorySet.setMemoryCells(
            0x1000u,
            byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x01.toByte(), 0x36.toByte()),
        )

        val decoded = Instructions.decode(0x1000u)

        assertEquals("SLL (IX+01h)", decoded.instruction.toString())
        assertEquals(4, decoded.instruction.bytes.size)
        assertEquals(3, decoded.opcodeFetchCount)
    }

    @Test
    fun executeShiftLeftLogicalOneMemoryIXd() {
        Registers.specialPurposeRegisters.setIX(0x2000.toShort())
        Memory.memorySet.setMemoryCell(0x2001u, 0x80.toByte())

        val instruction = SLLIXd(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x01.toByte(), 0x36.toByte())),
            displacement = 0x01.toByte(),
        )

        instruction.execute()

        assertEquals(0x01.toByte(), Memory.memorySet.getMemoryCell(0x2001u))
        assertTrue(Registers.registerSet.getCFlag())
        assertFalse(Registers.registerSet.getNFlag())
        assertFalse(Registers.registerSet.getHFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = SLLIXd(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xDD.toByte(), 0xCB.toByte(), 0x01.toByte(), 0x36.toByte())),
            displacement = 0x01.toByte(),
        )

        assertEquals("SLL (IX+01h)", instruction.toString())
    }
}
