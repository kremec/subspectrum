package com.subbyte.subspectrum.base

import androidx.compose.runtime.mutableStateOf
import com.subbyte.subspectrum.units.getBit
import com.subbyte.subspectrum.units.toBytes
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.SourceDataLine

internal actual object ULABeeper {
    private const val ULA_IO_PORT_LOW_BYTE = 0xFE.toByte()

    private const val SAMPLE_RATE = 44_100
    private const val BUFFER_FRAMES = 1_024
    private const val SAMPLE_BYTES = 2
    private const val HIGH_LEVEL_SAMPLE: Short = 12_000
    private const val MAX_CATCH_UP_MILLIS = 50L

    private val MAX_CATCH_UP_TSTATES = (ULATiming.CPU_CLOCK_HZ.toLong() * MAX_CATCH_UP_MILLIS) / 1_000L

    private val line: SourceDataLine? = createLine()

    private var lastTStates: Long? = null
    private var sampleRemainder: Long = 0
    private var levelHigh = false

    actual val isMuted = mutableStateOf(false)

    private val audioBuffer = ByteArray(BUFFER_FRAMES * SAMPLE_BYTES).apply {
        fillWithSample(HIGH_LEVEL_SAMPLE)
    }
    private val silenceBuffer = ByteArray(BUFFER_FRAMES * SAMPLE_BYTES)

    actual fun toggleMuted() {
        isMuted.value = !isMuted.value

        if (isMuted.value) {
            sampleRemainder = 0
        } else {
            lastTStates = ULATiming.totalTStates
        }
    }

    actual fun onPortWrite(portAddress: IOAddress, value: Byte) {
        if (portAddress.toBytes().second != ULA_IO_PORT_LOW_BYTE) {
            return
        }

        val nextLevelHigh = value.getBit(4)
        val nowTStates = ULATiming.totalTStates

        if (isMuted.value) {
            sampleRemainder = 0
            lastTStates = nowTStates
            levelHigh = nextLevelHigh
            return
        }

        val previousTStates = lastTStates
        if (previousTStates != null && nowTStates > previousTStates) {
            val elapsedTStates = nowTStates - previousTStates
            if (elapsedTStates <= MAX_CATCH_UP_TSTATES) {
                emitElapsedSamples(elapsedTStates)
            } else {
                sampleRemainder = 0
            }
        }

        lastTStates = nowTStates
        levelHigh = nextLevelHigh
    }

    actual fun reset() {
        lastTStates = null
        sampleRemainder = 0
        levelHigh = false
        line?.flush()
    }

    actual fun shutdown() {
        line?.drain()
        line?.stop()
        line?.close()

        lastTStates = null
        sampleRemainder = 0
        levelHigh = false
    }

    private fun emitElapsedSamples(elapsedTStates: Long) {
        val sourceLine = line ?: return

        val scaled = elapsedTStates * SAMPLE_RATE.toLong() + sampleRemainder
        var remainingFrames = (scaled / ULATiming.CPU_CLOCK_HZ).toInt()
        sampleRemainder = scaled % ULATiming.CPU_CLOCK_HZ

        if (remainingFrames <= 0) {
            return
        }

        val buffer = if (levelHigh) audioBuffer else silenceBuffer
        while (remainingFrames > 0) {
            val availableFrames = sourceLine.available() / SAMPLE_BYTES
            if (availableFrames <= 0) {
                break
            }

            val frames = remainingFrames
                .coerceAtMost(BUFFER_FRAMES)
                .coerceAtMost(availableFrames)

            if (frames <= 0) {
                break
            }

            sourceLine.write(buffer, 0, frames * SAMPLE_BYTES)
            remainingFrames -= frames
        }
    }

    private fun createLine(): SourceDataLine? {
        return try {
            val format = AudioFormat(
                SAMPLE_RATE.toFloat(),
                SAMPLE_BYTES * 8,
                1,
                true,
                false,
            )
            val sourceLine = AudioSystem.getSourceDataLine(format)
            sourceLine.open(format)
            sourceLine.start()
            sourceLine
        } catch (_: Exception) {
            null
        }
    }

    private fun ByteArray.fillWithSample(sample: Short) {
        val lowByte = (sample.toInt() and 0xFF).toByte()
        val highByte = ((sample.toInt() ushr 8) and 0xFF).toByte()

        var index = 0
        while (index < size) {
            this[index] = lowByte
            this[index + 1] = highByte
            index += SAMPLE_BYTES
        }
    }
}
