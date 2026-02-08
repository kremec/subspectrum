package com.subbyte.subspectrum.proc.instructions.load8

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.base.ULA
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.units.DataByteArray
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class LDARTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val instruction = LDAR.decode(0xED5FL, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(2, instruction.bytes.size)
        assertEquals(0xED.toByte(), instruction.bytes[0])
        assertEquals(0x5F.toByte(), instruction.bytes[1])
    }

    @Test
    fun executeLoadRToA() {
        Registers.specialPurposeRegisters.setR(0xAB.toByte())
        Registers.registerSet.setA(0x00.toByte())

        val instruction = LDAR(
            address = 0x1000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x5F.toByte()))
        )

        instruction.execute()

        assertEquals(0xAB.toByte(), Registers.registerSet.getA())
        assertFalse(Registers.registerSet.getHFlag())
        assertFalse(Registers.registerSet.getNFlag())
    }

    @Test
    fun executeLoadRToAInterruptClearsPV() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
        ULA.reset()

        Registers.specialPurposeRegisters.setR(0x01.toByte())
        Registers.specialPurposeRegisters.setPC(0x0000)
        Memory.memorySet.setMemoryCells(
            0x0000u,
            byteArrayOf(0xED.toByte(), 0x5F.toByte())
        )

        Processor.NMI_FF = false
        Processor.IFF1 = true
        Processor.IFF2 = true
        Processor.afterEIDI = false

        ULA.advanceCycles(ULA.T_STATES_PER_FRAME - 9)

        Processor.step()

        assertFalse(Registers.registerSet.getPVFlag())
    }

    @Test
    fun toStringFormat() {
        val instruction = LDAR(
            address = 0x0000u,
            bytes = DataByteArray(byteArrayOf(0xED.toByte(), 0x5F.toByte()))
        )

        assertEquals("LD A, R", instruction.toString())
    }
}
