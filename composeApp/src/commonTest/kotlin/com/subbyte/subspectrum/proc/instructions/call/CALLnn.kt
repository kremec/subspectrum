package com.subbyte.subspectrum.proc.instructions.call

import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CALLnnTest {
    @BeforeTest
    fun setup() {
        Memory.memorySet.reset()
        Registers.registerSet.reset()
        Registers.specialPurposeRegisters.reset()
    }

    @Test
    fun decodeInstruction() {
        val word = (0xCDL shl 16) or (0x3412L) // CD 34 12 (little-endian address)
        val instruction = CALLnn.decode(word, 0x1000u)

        assertEquals(0x1000u, instruction.address)
        assertEquals(3, instruction.bytes.size)
        assertEquals(0xCD.toByte(), instruction.bytes[0])
        assertEquals(0x34.toByte(), instruction.bytes[1])
        assertEquals(0x12.toByte(), instruction.bytes[2])

        val callnn = instruction as CALLnn
        assertEquals(0x1234u, callnn.targetAddress)
    }

    @Test
    fun executeCallPushesReturnAddress() {
        // Setup: PC at 0x1000, SP at 0xFFFE
        Registers.specialPurposeRegisters.setPC(0x1003) // PC after the 3-byte CALL instruction
        Registers.specialPurposeRegisters.setSP(0xFFFE.toShort())

        val instruction = CALLnn(
            address = 0x1000u,
            bytes = byteArrayOf(0xCD.toByte(), 0x56.toByte(), 0x78.toByte()),
            targetAddress = 0x5678u
        )

        instruction.execute()

        // PC should jump to target address
        assertEquals(0x5678, Registers.specialPurposeRegisters.getPC())
        
        // SP should be decremented by 2
        assertEquals(0xFFFC.toShort(), Registers.specialPurposeRegisters.getSP())
        
        // Return address (0x1003) should be pushed onto stack
        // Low byte at SP (0xFFFC), high byte at SP+1 (0xFFFD)
        assertEquals<Byte>(0x03.toByte(), Memory.memorySet.getMemoryCell(0xFFFCu))
        assertEquals<Byte>(0x10.toByte(), Memory.memorySet.getMemoryCell(0xFFFDu))
    }

    @Test
    fun executeCallToZeroAddress() {
        Registers.specialPurposeRegisters.setPC(0x2003)
        Registers.specialPurposeRegisters.setSP(0x8000.toShort())

        val instruction = CALLnn(
            address = 0x2000u,
            bytes = byteArrayOf(0xCD.toByte(), 0x00.toByte(), 0x00.toByte()),
            targetAddress = 0x0000u
        )

        instruction.execute()

        assertEquals(0x0000, Registers.specialPurposeRegisters.getPC())
        assertEquals(0x7FFE.toShort(), Registers.specialPurposeRegisters.getSP())
        
        // Return address 0x2003 on stack
        assertEquals<Byte>(0x03.toByte(), Memory.memorySet.getMemoryCell(0x7FFEu))
        assertEquals<Byte>(0x20.toByte(), Memory.memorySet.getMemoryCell(0x7FFFu))
    }

    @Test
    fun executeCallToHighAddress() {
        Registers.specialPurposeRegisters.setPC(0x0003)
        Registers.specialPurposeRegisters.setSP(0xC000.toShort())

        val instruction = CALLnn(
            address = 0x0000u,
            bytes = byteArrayOf(0xCD.toByte(), 0xFF.toByte(), 0xFF.toByte()),
            targetAddress = 0xFFFFu
        )

        instruction.execute()

        assertEquals(0xFFFF.toShort(), Registers.specialPurposeRegisters.getPC())
        assertEquals(0xBFFE.toShort(), Registers.specialPurposeRegisters.getSP())
        
        // Return address 0x0003 on stack
        assertEquals<Byte>(0x03.toByte(), Memory.memorySet.getMemoryCell(0xBFFEu))
        assertEquals<Byte>(0x00.toByte(), Memory.memorySet.getMemoryCell(0xBFFFu))
    }

    @Test
    fun executeNestedCalls() {
        // Test multiple nested CALL instructions to verify stack works correctly
        Registers.specialPurposeRegisters.setPC(0x1003)
        Registers.specialPurposeRegisters.setSP(0xFFFE.toShort())

        // First CALL
        val call1 = CALLnn(
            address = 0x1000u,
            bytes = byteArrayOf(0xCD.toByte(), 0x00.toByte(), 0x20.toByte()),
            targetAddress = 0x2000u
        )
        call1.execute()

        assertEquals(0x2000, Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFC.toShort(), Registers.specialPurposeRegisters.getSP())
        assertEquals<Byte>(0x03.toByte(), Memory.memorySet.getMemoryCell(0xFFFCu))
        assertEquals<Byte>(0x10.toByte(), Memory.memorySet.getMemoryCell(0xFFFDu))

        // Second nested CALL
        Registers.specialPurposeRegisters.setPC(0x2003) // After second CALL instruction
        val call2 = CALLnn(
            address = 0x2000u,
            bytes = byteArrayOf(0xCD.toByte(), 0x00.toByte(), 0x30.toByte()),
            targetAddress = 0x3000u
        )
        call2.execute()

        assertEquals(0x3000, Registers.specialPurposeRegisters.getPC())
        assertEquals(0xFFFA.toShort(), Registers.specialPurposeRegisters.getSP())
        
        // Stack should have both return addresses
        assertEquals<Byte>(0x03.toByte(), Memory.memorySet.getMemoryCell(0xFFFAu)) // 0x2003 low
        assertEquals<Byte>(0x20.toByte(), Memory.memorySet.getMemoryCell(0xFFFBu)) // 0x2003 high
        assertEquals<Byte>(0x03.toByte(), Memory.memorySet.getMemoryCell(0xFFFCu)) // 0x1003 low
        assertEquals<Byte>(0x10.toByte(), Memory.memorySet.getMemoryCell(0xFFFDu)) // 0x1003 high
    }

    @Test
    fun toStringFormat() {
        val instruction = CALLnn(
            address = 0x0000u,
            bytes = byteArrayOf(0xCD.toByte(), 0x34.toByte(), 0x12.toByte()),
            targetAddress = 0x1234u
        )

        assertEquals("CALL 1234h", instruction.toString())
    }

    @Test
    fun toStringFormatZero() {
        val instruction = CALLnn(
            address = 0x0000u,
            bytes = byteArrayOf(0xCD.toByte(), 0x00.toByte(), 0x00.toByte()),
            targetAddress = 0x0000u
        )

        assertEquals("CALL 0000h", instruction.toString())
    }

    @Test
    fun toStringFormatHighAddress() {
        val instruction = CALLnn(
            address = 0x0000u,
            bytes = byteArrayOf(0xCD.toByte(), 0xFF.toByte(), 0xFF.toByte()),
            targetAddress = 0xFFFFu
        )

        assertEquals("CALL FFFFh", instruction.toString())
    }
}
