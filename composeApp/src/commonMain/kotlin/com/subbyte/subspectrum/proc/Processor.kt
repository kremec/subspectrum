package com.subbyte.subspectrum.proc

import androidx.compose.runtime.mutableStateOf
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.base.ULATapeDeck
import com.subbyte.subspectrum.base.ULATiming
import com.subbyte.subspectrum.proc.instructions.Instructions
import com.subbyte.subspectrum.units.toBytes
import kotlinx.coroutines.delay
import kotlin.time.TimeSource

object Processor {
    var NMI_FF: Boolean = false
    var IFF1: Boolean = false
    var IFF2: Boolean = false
    var afterEIDI: Boolean = false
    var interruptMode: Int = 0

    var inHalt: Boolean = false

    var running = mutableStateOf(false)

    var breakpoints = mutableStateOf(setOf<Int>())
    var currentBreakpoint: Int? = null

    fun step() {
        val pc = Registers.specialPurposeRegisters.getPC()

        if (breakpoints.value.contains(pc.toInt())) {
            if (currentBreakpoint != pc.toInt()) {
                if (running.value) {
                    running.value = false
                    currentBreakpoint = pc.toInt()
                    return
                }
            } else {
                currentBreakpoint = null
            }
        }

        if (NMI_FF && !afterEIDI) {
            handleNMI()
        }
        inHalt = false
        afterEIDI = false

        if (ULATapeDeck.tryHandleLdBytesRoutine()) {
            Registers.specialPurposeRegisters.incrementR(1)
            ULATiming.advanceCycles(1)
            return
        }

        val decodedInstruction = Instructions.decode(pc.toUShort())
        Registers.specialPurposeRegisters.incrementR(decodedInstruction.opcodeFetchCount)

        val instruction = decodedInstruction.instruction
        Registers.specialPurposeRegisters.setPC((pc + instruction.bytes.size).toShort())

        instruction.execute()
        ULATiming.advanceCycles(instruction.getTStates())

        // Check for interrupt at the end of instruction (sampled on last T-state)
        if (IFF1 && !afterEIDI && ULATiming.isInterruptPending()) {
            ULATiming.clearInterrupt()
            handleInterrupt()
        }
    }

    private fun handleNMI() {
        NMI_FF = false
        IFF2 = IFF1
        IFF1 = false

        val spRegisterValue = Registers.specialPurposeRegisters.getSP()
        Registers.specialPurposeRegisters.setSP(spRegisterValue.minus(2).toShort())

        if (inHalt) {
            // If in halt, PC must point to instruction after halt
            val pcRegisterValue = Registers.specialPurposeRegisters.getPC()
            Registers.specialPurposeRegisters.setPC(pcRegisterValue.inc())
        }

        val pcRegisterValue = Registers.specialPurposeRegisters.getPC()
        val (highByte, lowByte) = pcRegisterValue.toBytes()
        Registers.specialPurposeRegisters.setSP(Registers.specialPurposeRegisters.getSP().minus(2).toShort())
        Memory.memorySet.setMemoryCells(
            Registers.specialPurposeRegisters.getSP().toUShort(),
            byteArrayOf(lowByte, highByte)
        )

        Registers.specialPurposeRegisters.setPC(0x0066)

        ULATiming.advanceNmiCycles()
    }

    private fun handleInterrupt() {
        IFF1 = false
        IFF2 = false

        if (inHalt) {
            // If in halt, PC must point to instruction after halt
            val pcRegisterValue = Registers.specialPurposeRegisters.getPC()
            Registers.specialPurposeRegisters.setPC(pcRegisterValue.inc())
            inHalt = false
        }

        when (interruptMode) {
            0 -> {
                // On Spectrum, IM0 commonly behaves like RST 0x38 because 0xFF is on data bus.
                handleInterruptMode1()
            }
            1 -> handleInterruptMode1()
            2 -> {
                // TODO: Implement interrupt mode 2 vector table lookup.
                ULATiming.advanceInterruptCycles(interruptMode)
            }
        }
    }

    private fun handleInterruptMode1() {
        val pcRegisterValue = Registers.specialPurposeRegisters.getPC()
        val (highByte, lowByte) = pcRegisterValue.toBytes()
        Registers.specialPurposeRegisters.setSP(Registers.specialPurposeRegisters.getSP().minus(2).toShort())
        Memory.memorySet.setMemoryCells(
            Registers.specialPurposeRegisters.getSP().toUShort(),
            byteArrayOf(lowByte, highByte)
        )

        Registers.specialPurposeRegisters.setPC(0x0038)

        ULATiming.advanceInterruptCycles(1)
    }

    suspend fun run() {
        running.value = true
        val startTime = TimeSource.Monotonic.markNow()
        val startTStates = ULATiming.totalTStates

        try {
            while (running.value) {
                step()

                if (ULATiming.isInstructionExecutionRealtime.value) {
                    continue
                }

                val stepElapsedTStates = ULATiming.totalTStates - startTStates
                val expectedNanos = (stepElapsedTStates * 1_000_000_000L) / ULATiming.CPU_CLOCK_HZ
                val actualNanos = startTime.elapsedNow().inWholeNanoseconds

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

        breakpoints.value = setOf()
        currentBreakpoint = null
    }
}
