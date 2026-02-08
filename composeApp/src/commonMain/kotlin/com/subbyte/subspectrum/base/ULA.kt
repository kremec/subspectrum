package com.subbyte.subspectrum.base

object ULA {
    const val T_STATES_PER_FRAME = 69888

    var currentTState: Int = 0
        private set

    private var interruptPending = false

    fun isInterruptPending(): Boolean {
        return interruptPending
    }

    fun clearInterrupt() {
        interruptPending = false
    }

    fun advanceCycles(cycles: Int) {
        val nextTState = currentTState + cycles
        if (nextTState >= T_STATES_PER_FRAME) {
            interruptPending = true
        }
        currentTState = nextTState % T_STATES_PER_FRAME
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
        currentTState = 0
        interruptPending = false
    }
}