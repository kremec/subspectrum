package com.subbyte.subspectrum.proc.instructions.control

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.Processor
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EITest {
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
        val instruction = EI.decode(0xFBL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(1, instruction.bytes.size)
        assertEquals(0xFB.toByte(), instruction.bytes[0])
    }

    @Test
    fun executeEnablesInterrupts() {
        Processor.IFF1 = false
        Processor.IFF2 = false
        Processor.afterEIDI = false

        val instruction = EI(
            address = 0x1000u,
            bytes = byteArrayOf(0xFB.toByte())
        )

        instruction.execute()

        assertTrue(Processor.IFF1)
        assertTrue(Processor.IFF2)
        assertTrue(Processor.afterEIDI)
    }

    @Test
    fun executeAlreadyEnabled() {
        Processor.IFF1 = true
        Processor.IFF2 = true
        Processor.afterEIDI = false

        val instruction = EI(
            address = 0x1000u,
            bytes = byteArrayOf(0xFB.toByte())
        )

        instruction.execute()

        assertTrue(Processor.IFF1)
        assertTrue(Processor.IFF2)
        assertTrue(Processor.afterEIDI)
    }

    @Test
    fun toStringFormat() {
        val instruction = EI(
            address = 0x0000u,
            bytes = byteArrayOf(0xFB.toByte())
        )

        assertEquals("EI", instruction.toString())
    }
}
