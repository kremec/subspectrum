package com.subbyte.subspectrum.base

import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.units.SpectrumTapeDataBlock
import com.subbyte.subspectrum.units.SpectrumTapeImage
import com.subbyte.subspectrum.units.Word

private const val LD_BYTES_ENTRY_ADDRESS: Word = 0x0556
private const val HEADER_PROCESSING_START_ADDRESS: Word = 0x0775
private const val HEADER_PROCESSING_SKIP_TARGET_ADDRESS: Word = 0x07B7
private const val SA_LD_RET_ADDRESS: Word = 0x053F

private const val TAPE_HEADER_FLAG: Byte = 0x00
private const val TAPE_DATA_FLAG: Byte = 0xFF.toByte()
private const val TAPE_HEADER_PAYLOAD_LENGTH: Word = 17

private enum class TapeOperation(val loadMode: Boolean) {
    LOAD(true),
    VERIFY(false),
}

object ULATapeDeck {
    private var insertedTapeImage: SpectrumTapeImage? = null
    private var readHeadBlockIndex: Int = 0
    private var pendingDataBlockIndex: Int? = null
    private var skipNextRomHeaderProcessing: Boolean = false

    fun insertTape(tapeImage: SpectrumTapeImage) {
        insertedTapeImage = tapeImage
        resetLoadState()
    }

    fun reset() {
        insertedTapeImage = null
        resetLoadState()
    }

    private fun resetLoadState() {
        readHeadBlockIndex = 0
        pendingDataBlockIndex = null
        skipNextRomHeaderProcessing = false
    }

    fun tryBypassRomHeaderProcessing(pc: Word): Word? {
        if (!skipNextRomHeaderProcessing || pc != HEADER_PROCESSING_START_ADDRESS) {
            return null
        }

        skipNextRomHeaderProcessing = false
        return HEADER_PROCESSING_SKIP_TARGET_ADDRESS
    }

    fun tryHandleLdBytesRoutine(): Boolean {
        val tape = insertedTapeImage ?: return false
        if (Registers.specialPurposeRegisters.getPC() != LD_BYTES_ENTRY_ADDRESS) {
            return false
        }

        val expectedFlag = Registers.registerSet.getA()
        val tapeOperation = TapeOperation.entries.first { it.loadMode == Registers.registerSet.getCFlag() }
        val expectedLength = Registers.registerSet.getDE()
        val destinationAddress = Registers.specialPurposeRegisters.getIX()

        val tapeBlock = selectBlockForRomCall(
            tape = tape,
            expectedFlag = expectedFlag,
            expectedLength = expectedLength,
        ) ?: run {
            finishLdBytesCall(success = false)
            return true
        }

        val payload = tapeBlock.payload
        if (tapeOperation == TapeOperation.LOAD) {
            Memory.memorySet.setMemoryCells(destinationAddress.toUShort(), payload)
        } else {
            val memoryValues = if (payload.isEmpty()) {
                byteArrayOf()
            } else {
                Memory.memorySet.getMemoryCells(
                    destinationAddress.toUShort(),
                    destinationAddress.plus(payload.size - 1).toUShort(),
                )
            }

            if (!memoryValues.contentEquals(payload)) {
                finishLdBytesCall(success = false)
                return true
            }
        }

        Registers.registerSet.setDE(0x0000)
        Registers.specialPurposeRegisters.setIX(destinationAddress.plus(expectedLength).toShort())
        Registers.registerSet.setA(0x00.toByte())

        if (expectedFlag == TAPE_HEADER_FLAG && expectedLength == TAPE_HEADER_PAYLOAD_LENGTH && tapeOperation == TapeOperation.LOAD) {
            skipNextRomHeaderProcessing = true
        }

        finishLdBytesCall(success = true)
        return true
    }

    private fun selectBlockForRomCall(
        tape: SpectrumTapeImage,
        expectedFlag: Byte,
        expectedLength: Word,
    ): SpectrumTapeDataBlock? {
        val blocks = tape.blocks

        if (expectedFlag == TAPE_HEADER_FLAG && expectedLength == TAPE_HEADER_PAYLOAD_LENGTH) {
            val headerIndex = findNextBlockIndex(blocks, readHeadBlockIndex) { block ->
                block.flag.toByte() == TAPE_HEADER_FLAG &&
                        block.payload.size == TAPE_HEADER_PAYLOAD_LENGTH.toInt()
            } ?: run {
                pendingDataBlockIndex = null
                return null
            }

            return consumeBlock(
                tape = tape,
                blockIndex = headerIndex,
                nextPendingDataBlockIndex = blocks[headerIndex].pairedDataBlockIndex,
            )
        }

        if (expectedFlag == TAPE_DATA_FLAG) {
            val pendingIndex = pendingDataBlockIndex
            pendingDataBlockIndex = null

            if (pendingIndex != null && pendingIndex in blocks.indices && blocks[pendingIndex].flag.toByte() == TAPE_DATA_FLAG) {
                return consumeBlock(
                    tape = tape,
                    blockIndex = pendingIndex,
                    nextPendingDataBlockIndex = null,
                )
            }

            val dataIndex = findNextBlockIndex(blocks, readHeadBlockIndex) { block ->
                block.flag.toByte() == TAPE_DATA_FLAG
            } ?: run {
                pendingDataBlockIndex = null
                return null
            }

            return consumeBlock(
                tape = tape,
                blockIndex = dataIndex,
                nextPendingDataBlockIndex = null,
            )
        }

        val matchingIndex = findNextBlockIndex(blocks, readHeadBlockIndex) { block ->
            block.flag.toByte() == expectedFlag &&
                    block.payload.size == expectedLength.toInt()
        } ?: run {
            pendingDataBlockIndex = null
            return null
        }

        return consumeBlock(
            tape = tape,
            blockIndex = matchingIndex,
            nextPendingDataBlockIndex = null,
        )
    }

    private fun consumeBlock(
        tape: SpectrumTapeImage,
        blockIndex: Int,
        nextPendingDataBlockIndex: Int?,
    ): SpectrumTapeDataBlock {
        readHeadBlockIndex = blockIndex + 1
        pendingDataBlockIndex = nextPendingDataBlockIndex
        return tape.blocks[blockIndex]
    }

    private inline fun findNextBlockIndex(
        blocks: List<SpectrumTapeDataBlock>,
        startIndex: Int,
        matches: (SpectrumTapeDataBlock) -> Boolean,
    ): Int? {
        if (blocks.isEmpty()) {
            return null
        }

        val safeStart = startIndex.coerceIn(0, blocks.size)
        for (index in safeStart until blocks.size) {
            if (matches(blocks[index])) {
                return index
            }
        }
        for (index in 0 until safeStart) {
            if (matches(blocks[index])) {
                return index
            }
        }
        return null
    }

    private fun finishLdBytesCall(success: Boolean) {
        Registers.registerSet.setCFlag(success)

        Processor.IFF1 = false
        Processor.IFF2 = false
        Processor.afterEIDI = false

        Registers.specialPurposeRegisters.setPC(SA_LD_RET_ADDRESS)
    }
}
