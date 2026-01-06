package com.subbyte.subspectrum.proc.instructions.load16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDIYMEMnnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDIYMEMnn.decode(0xFD2ACD12L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(4, instruction.bytes.size)
        assertEquals(0xFD.toByte(), instruction.bytes[0])
        assertEquals(0x2A.toByte(), instruction.bytes[1])
        assertEquals(0xCD.toByte(), instruction.bytes[2])
        assertEquals(0x12.toByte(), instruction.bytes[3])

        val ldnn = instruction as LDIYMEMnn
        assertEquals(0x12CD.toShort(), ldnn.sourceWord)
    }

    @Test
    fun executeLoadMemoryToIY() {
        Memory.memorySet.setMemoryCell(0x1234u, 0xCD.toByte())
        Memory.memorySet.setMemoryCell(0x1235u, 0xAB.toByte())

        val instruction = LDIYMEMnn(
            address = 0x1000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x2A.toByte(), 0x34.toByte(), 0x12.toByte()),
            sourceWord = 0x1234.toShort()
        )

        instruction.execute()

        assertEquals(0xABCD.toShort(), Registers.specialPurposeRegisters.getIY())
    }

    @Test
    fun toStringFormat() {
        val instruction = LDIYMEMnn(
            address = 0x0000u,
            bytes = byteArrayOf(0xFD.toByte(), 0x2A.toByte(), 0x34.toByte(), 0x12.toByte()),
            sourceWord = 0x1234.toShort()
        )

        assertEquals("LD IY, (1234h)", instruction.toString())
    }
}
