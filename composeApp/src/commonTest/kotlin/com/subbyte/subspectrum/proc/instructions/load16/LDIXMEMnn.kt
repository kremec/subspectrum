package com.subbyte.subspectrum.proc.instructions.load16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDIXMEMnnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDIXMEMnn.decode(0xDD2ACDABL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(4, instruction.bytes.size)
        assertEquals(0xDD.toByte(), instruction.bytes[0])
        assertEquals(0x2A.toByte(), instruction.bytes[1])
        assertEquals(0xCD.toByte(), instruction.bytes[2])
        assertEquals(0xAB.toByte(), instruction.bytes[3])

        val ldnn = instruction as LDIXMEMnn
        assertEquals(0xABCD.toUShort(), ldnn.sourceUWord)
    }

    @Test
    fun executeLoadMemoryToIX() {
        Memory.memorySet.setMemoryCell(0x1234u, 0xCD.toByte())
        Memory.memorySet.setMemoryCell(0x1235u, 0xAB.toByte())

        val instruction = LDIXMEMnn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xDD.toByte(), 0x2A.toByte(), 0x34.toByte(), 0x12.toByte())),
            sourceUWord = 0x1234.toUShort()
        )

        instruction.execute()

        assertEquals(0xABCD.toShort(), Registers.specialPurposeRegisters.getIX())
    }

    @Test
    fun toStringFormat() {
        val instruction = LDIXMEMnn(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xDD.toByte(), 0x2A.toByte(), 0x34.toByte(), 0x12.toByte())),
            sourceUWord = 0x1234.toUShort()
        )

        assertEquals("LD IX, (1234h)", instruction.toString())
    }
}
