package com.subbyte.subspectrum.base

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object ULA {
    const val CPU_CLOCK_HZ = 3_500_000
    const val T_STATES_PER_FRAME = 69888
    const val FRAMES_PER_SCREEN_FLASH_ATTRIBUTE_PHASE = 32

    var currentTStatesInFrame: Int = 0
        private set
    var totalTStates: Long = 0
        private set
    var frameCount: Long = 0
        private set

    private var interruptPending = false

    private val _isInstructionExecutionRealtime = mutableStateOf(false)
    val isInstructionExecutionRealtime: Boolean
        get() = _isInstructionExecutionRealtime.value

    private val _frameInvalidations = MutableSharedFlow<Long>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val frameInvalidations: SharedFlow<Long> = _frameInvalidations.asSharedFlow()
    init {
        _frameInvalidations.tryEmit(frameCount)
    }

    fun isInterruptPending(): Boolean {
        return interruptPending
    }

    fun clearInterrupt() {
        interruptPending = false
    }

    fun advanceCycles(cycles: Int) {
        val newTStatesInFrame = currentTStatesInFrame + cycles
        val frameNumber = newTStatesInFrame / T_STATES_PER_FRAME
        if (frameNumber > 0) {
            interruptPending = true
            frameCount += frameNumber.toLong()
            _frameInvalidations.tryEmit(frameCount)
        }
        currentTStatesInFrame = newTStatesInFrame % T_STATES_PER_FRAME
        totalTStates += cycles.toLong()
    }

    fun advanceNmiCycles() {
        advanceCycles(11)
    }

    fun advanceInterruptCycles(mode: Int) {
        val cycles = when (mode) {
            0 -> 0
            1 -> 13
            2 -> 19
            else -> 0
        }
        advanceCycles(cycles)
    }

    fun reset() {
        currentTStatesInFrame = 0
        totalTStates = 0
        frameCount = 0

        interruptPending = false

        _frameInvalidations.tryEmit(frameCount)
    }

    fun isScreenFlashAttributeInverted(): Boolean {
        val flashPhase = frameCount / FRAMES_PER_SCREEN_FLASH_ATTRIBUTE_PHASE
        val isOddPhase = flashPhase % 2L == 1L
        return isOddPhase
    }
}
