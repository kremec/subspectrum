package com.subbyte.subspectrum.proc

import androidx.compose.runtime.mutableStateOf
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.base.ULATapeDeck
import com.subbyte.subspectrum.base.ULATiming
import com.subbyte.subspectrum.proc.instructions.Instructions
import com.subbyte.subspectrum.units.toBytes
import com.subbyte.subspectrum.units.wordFromBytes
import kotlinx.coroutines.delay
import kotlin.time.TimeSource

object Processor {
    private const val INTERRUPT_DATA_BUS_VALUE: Byte = 0xFF.toByte()

    var NMI_FF: Boolean = false
    var IFF1: Boolean = false
    var IFF2: Boolean = false
    var afterEIDI: Boolean = false
    var interruptMode: Int = 0

    var inHalt: Boolean = false

    var running = mutableStateOf(false)

    var breakpoints = mutableStateOf(emptySet<Address>())
    var currentBreakpoint: Address? = null
    var totalInstructionsExecuted: Long = 0

    fun step() {
        val initialPc = Registers.specialPurposeRegisters.getPC()
        val initialPcAddress = initialPc.toUShort()

        if (breakpoints.value.contains(initialPcAddress)) {
            if (currentBreakpoint != initialPcAddress) {
                if (running.value) {
                    running.value = false
                    currentBreakpoint = initialPcAddress
                    return
                }
            } else {
                currentBreakpoint = null
            }
        }

        if (NMI_FF) {
            handleNMI()
            inHalt = false
            afterEIDI = false
            return
        }
        inHalt = false
        afterEIDI = false

        val headerBypassTarget = ULATapeDeck.tryBypassRomHeaderProcessing(Registers.specialPurposeRegisters.getPC())
        if (headerBypassTarget != null) {
            Registers.specialPurposeRegisters.setPC(headerBypassTarget)
        }

        if (ULATapeDeck.tryHandleLdBytesRoutine()) {
            Registers.specialPurposeRegisters.incrementR(1)
            ULATiming.advanceCycles(1)
            return
        }

        val pc = Registers.specialPurposeRegisters.getPC()

        val decodedInstruction = Instructions.decode(pc.toUShort())
        Registers.specialPurposeRegisters.incrementR(decodedInstruction.opcodeFetchCount)

        val instruction = decodedInstruction.instruction
        Registers.specialPurposeRegisters.setPC((pc + instruction.bytes.size).toShort())

        instruction.execute()
        totalInstructionsExecuted += 1
        ULATiming.advanceCycles(instruction.getTStates())

        // Check for interrupt at the end of instruction (sampled on last T-state)
        if (IFF1 && !afterEIDI && ULATiming.isInterruptPending()) {
            ULATiming.clearInterrupt()
            handleInterrupt()
        }
    }

    private fun handleNMI() {
        NMI_FF = false
        IFF1 = false
        Registers.specialPurposeRegisters.incrementR(1)

        if (inHalt) {
            // If in halt, PC must point to instruction after halt
            val pcRegisterValue = Registers.specialPurposeRegisters.getPC()
            Registers.specialPurposeRegisters.setPC(pcRegisterValue.inc())
        }

        pushPCToStack()

        Registers.specialPurposeRegisters.setPC(0x0066)

        ULATiming.advanceNmiCycles()
    }

    private fun handleInterrupt() {
        IFF1 = false
        IFF2 = false
        Registers.specialPurposeRegisters.incrementR(1)

        if (inHalt) {
            // If in halt, PC must point to instruction after halt
            val pcRegisterValue = Registers.specialPurposeRegisters.getPC()
            Registers.specialPurposeRegisters.setPC(pcRegisterValue.inc())
            inHalt = false
        }

        when (interruptMode) {
            0 -> {
                handleInterruptMode0()
            }
            1 -> handleInterruptMode1()
            2 -> {
                handleInterruptMode2()
            }
        }
    }

    private fun handleInterruptMode0() {
        val restartAddress = when (INTERRUPT_DATA_BUS_VALUE) {
            0xC7.toByte() -> 0x0000.toShort()
            0xCF.toByte() -> 0x0008.toShort()
            0xD7.toByte() -> 0x0010.toShort()
            0xDF.toByte() -> 0x0018.toShort()
            0xE7.toByte() -> 0x0020.toShort()
            0xEF.toByte() -> 0x0028.toShort()
            0xF7.toByte() -> 0x0030.toShort()
            0xFF.toByte() -> 0x0038.toShort()
            else -> 0x0038.toShort()
        }

        pushPCToStack()
        Registers.specialPurposeRegisters.setPC(restartAddress)

        ULATiming.advanceInterruptCycles(1)
    }

    private fun handleInterruptMode1() {
        pushPCToStack()
        Registers.specialPurposeRegisters.setPC(0x0038)

        ULATiming.advanceInterruptCycles(1)
    }

    private fun handleInterruptMode2() {
        pushPCToStack()

        val vectorAddress = Pair(
            Registers.specialPurposeRegisters.getI(),
            INTERRUPT_DATA_BUS_VALUE,
        ).wordFromBytes()
        val vectorBytes = Memory.memorySet.getMemoryCells(
            vectorAddress.toUShort(),
            vectorAddress.plus(1).toUShort(),
        )
        Registers.specialPurposeRegisters.setPC(Pair(vectorBytes[1], vectorBytes[0]).wordFromBytes())

        ULATiming.advanceInterruptCycles(2)
    }

    private fun pushPCToStack() {
        val pcRegisterValue = Registers.specialPurposeRegisters.getPC()
        val (highByte, lowByte) = pcRegisterValue.toBytes()
        Registers.specialPurposeRegisters.setSP(Registers.specialPurposeRegisters.getSP().minus(2).toShort())
        Memory.memorySet.setMemoryCells(
            Registers.specialPurposeRegisters.getSP().toUShort(),
            byteArrayOf(lowByte, highByte)
        )
    }

    suspend fun run() {
        running.value = true
        var pacingStartTime = TimeSource.Monotonic.markNow()
        var pacingStartTStates = ULATiming.totalTStates
        var previousRealtimeMode = ULATiming.isInstructionExecutionRealtime.value

        try {
            while (running.value) {
                step()

                val realtimeMode = ULATiming.isInstructionExecutionRealtime.value
                if (realtimeMode != previousRealtimeMode) {
                    pacingStartTime = TimeSource.Monotonic.markNow()
                    pacingStartTStates = ULATiming.totalTStates
                    previousRealtimeMode = realtimeMode
                }

                if (realtimeMode) {
                    continue
                }

                val stepElapsedTStates = ULATiming.totalTStates - pacingStartTStates
                val expectedNanos = (stepElapsedTStates * 1_000_000_000L) / ULATiming.CPU_CLOCK_HZ
                val actualNanos = pacingStartTime.elapsedNow().inWholeNanoseconds

                val behind = expectedNanos - actualNanos
                if (behind > 0) {
                    delay((behind + 999_999L) / 1_000_000L)
                }
            }
        } finally {
            running.value = false
        }
    }

    fun stop() {
        running.value = false
    }

    fun reset() {
        NMI_FF= false
        IFF1= false
        IFF2= false
        afterEIDI= false
        interruptMode= 0

        inHalt= false

        running.value = false

        breakpoints.value = emptySet()
        currentBreakpoint = null
        totalInstructionsExecuted = 0
    }
}
