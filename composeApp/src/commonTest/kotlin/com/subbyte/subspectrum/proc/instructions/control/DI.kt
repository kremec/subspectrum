package com.subbyte.subspectrum.proc.instructions.control

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.*

class DITest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
        Processor.IFF1 = false
        Processor.IFF2 = false
        Processor.afterEIDI = false
    }

    @Test
    fun decodeInstruction() {
        val instruction = DI.decode(0xF3L, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0xF3.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeDisablesInterrupts() {
        Processor.IFF1 = true
        Processor.IFF2 = true
        Processor.afterEIDI = false

        val instruction = DI(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xF3.toByte()))
        )

        instruction.execute()

        assertFalse(Processor.IFF1)
        assertFalse(Processor.IFF2)
        assertTrue(Processor.afterEIDI)
    }

    @Test
    fun executeAlreadyDisabled() {
        Processor.IFF1 = false
        Processor.IFF2 = false
        Processor.afterEIDI = false

        val instruction = DI(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xF3.toByte()))
        )

        instruction.execute()

        assertFalse(Processor.IFF1)
        assertFalse(Processor.IFF2)
        assertTrue(Processor.afterEIDI)
    }

    @Test
    fun toStringFormat() {
        val instruction = DI(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xF3.toByte()))
        )

        assertEquals("DI", instruction.toString())
    }
}
