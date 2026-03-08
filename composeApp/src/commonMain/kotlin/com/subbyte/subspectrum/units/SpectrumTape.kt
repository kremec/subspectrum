package com.subbyte.subspectrum.units

data class SpectrumTapeImage(
    val blocks: List<SpectrumTapeDataBlock>,
)

data class SpectrumTapeDataBlock(
    val flag: UByte,
    val payload: ByteArray,
    val checksum: UByte,
    val pairedDataBlockIndex: Int? = null,
) {
    val checksumIsValid: Boolean
        get() = checksum == calculateChecksum(flag, payload)

    companion object {
        fun fromRawBlock(rawData: ByteArray): SpectrumTapeDataBlock {
            require(rawData.size >= 2) {
                "Tape data block must contain flag and checksum bytes"
            }

            val flag = rawData.first().toUByte()
            val checksum = rawData.last().toUByte()
            val payload = rawData.copyOfRange(1, rawData.size - 1)

            return SpectrumTapeDataBlock(
                flag = flag,
                payload = payload,
                checksum = checksum,
            )
        }

        private fun calculateChecksum(flag: UByte, payload: ByteArray): UByte {
            var checksum = flag.toInt()
            for (byteValue in payload) {
                checksum = checksum xor (byteValue.toInt() and 0xFF)
            }
            return checksum.toUByte()
        }
    }
}
