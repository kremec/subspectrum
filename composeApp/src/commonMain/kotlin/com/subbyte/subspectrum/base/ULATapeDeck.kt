package com.subbyte.subspectrum.base

import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.units.SpectrumTapeDataBlock
import com.subbyte.subspectrum.units.SpectrumTapeImage
import com.subbyte.subspectrum.units.Word
import com.subbyte.subspectrum.units.wordFromBytes

private const val LD_BYTES_ENTRY_ADDRESS: Word = 0x0556

private enum class TapeOperation (val value: Boolean) {
    LOAD(true),
    VERIFY(false)
}

object ULATapeDeck {
    private var insertedTapeImage: SpectrumTapeImage? = null
    private var readHeadBlockIndex: Int = 0

    fun insertTape(tapeImage: SpectrumTapeImage) {
        insertedTapeImage = tapeImage
        readHeadBlockIndex = 0
    }

    fun getInsertedTape(): SpectrumTapeImage? {
        return insertedTapeImage
    }

    fun ejectTape() {
        insertedTapeImage = null
        readHeadBlockIndex = 0
    }

    private fun readNextBlockForRomLoading(): SpectrumTapeDataBlock? {
        val currentTape = insertedTapeImage ?: return null
        if (readHeadBlockIndex >= currentTape.blocks.size) {
            return null
        }

        val block = currentTape.blocks[readHeadBlockIndex]
        readHeadBlockIndex += 1
        return block
    }

    fun tryHandleLdBytesRoutine(): Boolean {
        val isTapeInserted = getInsertedTape() != null
        val atLDBYTESROMRoutine = Registers.specialPurposeRegisters.getPC() == LD_BYTES_ENTRY_ADDRESS
        if (!isTapeInserted || !atLDBYTESROMRoutine) {
            return false
        }

        val expectedFlag = Registers.registerSet.getA()
        val tapeOperation: TapeOperation = TapeOperation.entries.first { it.value == Registers.registerSet.getCFlag() }
        val expectedLength = Registers.registerSet.getDE()
        val destinationAddress = Registers.specialPurposeRegisters.getIX()

        val tapeBlock = readNextBlockForRomLoading()
        val tapeBlockExists = tapeBlock != null
        val tapeBlockChecksumValid = tapeBlock?.checksumIsValid == true
        val tapeBlockHasExpectedFlag = tapeBlock?.flag?.toByte() == expectedFlag
        val tapeBlockHasExpectedLength = tapeBlock?.payload?.size?.toShort() == expectedLength
        if (!tapeBlockExists || !tapeBlockChecksumValid || !tapeBlockHasExpectedFlag || !tapeBlockHasExpectedLength) {
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
                    destinationAddress.plus(payload.size - 1).toUShort()
                )
            }
            val correctMemoryValues = memoryValues.contentEquals(payload)
            if (!correctMemoryValues) {
                finishLdBytesCall(success = false)
                return true
            }
        }

        Registers.registerSet.setDE(0x0000)
        Registers.specialPurposeRegisters.setIX(destinationAddress.plus(expectedLength).toShort())
        Registers.registerSet.setA(0x00.toByte())

        finishLdBytesCall(success = true)
        return true
    }

    private fun finishLdBytesCall(success: Boolean) {
        Registers.registerSet.setCFlag(success)

        Processor.IFF1 = true
        Processor.IFF2 = true
        Processor.afterEIDI = true

        val sp = Registers.specialPurposeRegisters.getSP()
        Registers.specialPurposeRegisters.setSP(sp.plus(2).toShort())

        val returnAddressBytes = Memory.memorySet.getMemoryCells(sp.toUShort(), sp.inc().toUShort())
        Registers.specialPurposeRegisters.setPC(Pair(returnAddressBytes[1], returnAddressBytes[0]).wordFromBytes())
    }
}
