package com.subbyte.subspectrum.proc.instructions.load16

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LDHLnnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDHLnn.decode(0x2ACD12L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0x2A.toByte(), instruction.bytes[0])
        assertEquals(0xCD.toByte(), instruction.bytes[1])
        assertEquals(0x12.toByte(), instruction.bytes[2])

        val ldnn = instruction as LDHLnn
        assertEquals(0x12CD.toUShort(), ldnn.sourceUWord)
    }

    @Test
    fun executeLoadMemoryToHL() {
        Memory.memorySet.setMemoryCell(0x1234u, 0xAB.toByte())
        Memory.memorySet.setMemoryCell(0x1235u, 0xCD.toByte())

        val instruction = LDHLnn(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0x2A, 0x34.toByte(), 0x12.toByte())),
            sourceUWord = 0x1234.toUShort()
        )

        instruction.execute()

        assertEquals(0xCD.toByte(), Registers.registerSet.getH())
        assertEquals(0xAB.toByte(), Registers.registerSet.getL())
    }

    @Test
    fun toStringFormat() {
        val instruction = LDHLnn(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0x2A, 0x34.toByte(), 0x12.toByte())),
            sourceUWord = 0x1234.toUShort()
        )

        assertEquals("LD HL, (1234h)", instruction.toString())
    }
}
