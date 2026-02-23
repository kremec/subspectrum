package com.subbyte.subspectrum.base

import com.subbyte.subspectrum.units.SpectrumTapeDataBlock
import com.subbyte.subspectrum.units.SpectrumTapeImage

private val TZX_SIGNATURE = byteArrayOf(
    'Z'.code.toByte(),
    'X'.code.toByte(),
    'T'.code.toByte(),
    'a'.code.toByte(),
    'p'.code.toByte(),
    'e'.code.toByte(),
    '!'.code.toByte(),
    0x1A,
)

object ULATapeParser {
    fun parse(fileBytes: ByteArray): SpectrumTapeImage {
        val reader = TzxReader(fileBytes)

        validateHeader(reader)

        val dataBlocks = mutableListOf<SpectrumTapeDataBlock>()
        while (!reader.isAtEnd()) {
            when (val blockId = reader.readByte1()) {
                0x10 -> { // Standard speed data block
                    parseStandardSpeedDataBlock(reader, dataBlocks)
                }
                0x11 -> { // Turbo speed data block
                    parseTurboSpeedDataBlock(reader, dataBlocks)
                }
                0x12 -> { // Pure tone
                    reader.skip(4)
                }
                0x13 -> { // Sequence of pulses of various lengths
                    val pulseCount = reader.readByte1()
                    reader.skip(pulseCount * 2)
                }
                0x14 -> { // Pure data block
                    parsePureDataBlock(reader, dataBlocks)
                }
                0x15 -> { // Direct recording block
                    reader.skip(5)
                    val dataSampleLength = reader.readByte3()
                    reader.skip(dataSampleLength)
                }
                0x18 -> { // CSW recording block
                    val blockLength = reader.readByte4()
                    reader.skip(blockLength)
                }
                0x19 -> { // Generalized data block
                    val blockLength = reader.readByte4()
                    reader.skip(blockLength)
                }
                0x20 -> { // Pause (silence) or 'Stop the tape' command
                    reader.skip(2)
                }
                0x21 -> { // Group start
                    val groupNameStringLength = reader.readByte1()
                    reader.skip(groupNameStringLength)
                }
                0x22 -> { // Group end
                    // Empty
                }
                0x23 -> { // Jump to block
                    reader.skip(2)
                }
                0x24 -> { // Loop start
                    reader.skip(2)
                }
                0x25 -> { // Loop end
                    // Empty
                }
                0x26 -> { // Call sequence
                    val callCount = reader.readByte2()
                    reader.skip(callCount * 2)
                }
                0x27 -> { // Return from sequence
                    // Empty
                }
                0x28 -> { // Select block
                    val blockLength = reader.readByte2()
                    reader.skip(blockLength)
                }
                0x2A -> { // Stop the tape if in 48K mode
                    reader.skip(4)
                }
                0x2B -> { // Set signal level
                    val blockLength = reader.readByte4()
                    reader.skip(blockLength)
                }
                0x30 -> { // Text description
                    val textLength = reader.readByte1()
                    reader.skip(textLength)
                }
                0x31 -> { // Message block
                    reader.skip(1)
                    val textLength = reader.readByte1()
                    reader.skip(textLength)
                }
                0x32 -> { // Archive info
                    val blockLength = reader.readByte2()
                    reader.skip(blockLength)
                }
                0x33 -> { // Hardware info
                    val hardwareInfoCount = reader.readByte1()
                    reader.skip(hardwareInfoCount * 3)
                }
                0x35 -> { // Custom info block
                    reader.skip(10)
                    val customInfoLength = reader.readByte4()
                    reader.skip(customInfoLength)
                }
                0x5A -> { // "Glue" block (90 dec, ASCII Letter 'Z')
                    reader.skip(9)
                }

                0x16 -> { // C64 ROM Type Data Block
                    val inclusiveBlockLength = reader.readByte4()
                    reader.skip(inclusiveBlockLength - 4)
                }
                0x17 -> { // C64 Turbo Tape Data Block
                    val inclusiveBlockLength = reader.readByte4()
                    reader.skip(inclusiveBlockLength - 4)
                }
                0x34 -> { // Emulation info
                    reader.skip(8)
                }
                0x40 -> { // Snapshot block
                    reader.skip(1)
                    val snapshotLength = reader.readByte3()
                    reader.skip(snapshotLength)
                }

                else -> throw IllegalArgumentException("Unsupported TZX block id: 0x${blockId.toHexString(HexFormat.UpperCase).padStart(2, '0')}")
            }
        }

        return SpectrumTapeImage(dataBlocks)
    }

    private fun validateHeader(reader: TzxReader) {
        val signature = reader.readBytes(TZX_SIGNATURE.size)
        if (!signature.contentEquals(TZX_SIGNATURE)) {
            throw IllegalArgumentException("Invalid TZX signature")
        }

        // Skip TZX major and minor revision numbers (2x BYTE)
        reader.skip(2)
    }

    private fun parseStandardSpeedDataBlock(
        reader: TzxReader,
        dataBlocks: MutableList<SpectrumTapeDataBlock>,
    ) {
        reader.skip(2)

        val dataLength = reader.readByte2()
        val rawData = reader.readBytes(dataLength)
        dataBlocks += SpectrumTapeDataBlock.fromRawBlock(rawData)
    }

    private fun parseTurboSpeedDataBlock(
        reader: TzxReader,
        dataBlocks: MutableList<SpectrumTapeDataBlock>,
    ) {
        reader.skip(15)

        val dataLength = reader.readByte3()
        val rawData = reader.readBytes(dataLength)
        dataBlocks += SpectrumTapeDataBlock.fromRawBlock(rawData)
    }

    private fun parsePureDataBlock(
        reader: TzxReader,
        dataBlocks: MutableList<SpectrumTapeDataBlock>,
    ) {
        reader.skip(7)

        val dataLength = reader.readByte3()
        val rawData = reader.readBytes(dataLength)
        dataBlocks += SpectrumTapeDataBlock.fromRawBlock(rawData)
    }
}

private class TzxReader(
    private val bytes: ByteArray,
    private var offset: Int = 0
) {
    fun isAtEnd(): Boolean {
        return offset >= bytes.size
    }

    fun readByte1(): Int {
        requireAvailable(1)
        return bytes[offset++].toInt() and 0xFF
    }

    fun readByte2(): Int {
        requireAvailable(2)
        val low = bytes[offset].toInt() and 0xFF
        val high = bytes[offset + 1].toInt() and 0xFF
        offset += 2
        return low or (high shl 8)
    }

    fun readByte3(): Int {
        requireAvailable(3)
        val byte0 = bytes[offset].toInt() and 0xFF
        val byte1 = bytes[offset + 1].toInt() and 0xFF
        val byte2 = bytes[offset + 2].toInt() and 0xFF
        offset += 3
        return byte0 or (byte1 shl 8) or (byte2 shl 16)
    }

    fun readByte4(): Int {
        requireAvailable(4)
        val byte0 = bytes[offset].toInt() and 0xFF
        val byte1 = bytes[offset + 1].toInt() and 0xFF
        val byte2 = bytes[offset + 2].toInt() and 0xFF
        val byte3 = bytes[offset + 3].toInt() and 0xFF
        offset += 4
        return byte0 or (byte1 shl 8) or (byte2 shl 16) or (byte3 shl 24)
    }

    fun readBytes(length: Int): ByteArray {
        requireAvailable(length)
        val data = bytes.copyOfRange(offset, offset + length)
        offset += length
        return data
    }

    fun skip(length: Int) {
        requireAvailable(length)
        offset += length
    }

    private fun requireAvailable(length: Int) {
        require(length >= 0) { "Negative read length: $length" }
        if (offset + length > bytes.size) {
            throw IllegalArgumentException("Unexpected end of TZX data")
        }
    }
}
