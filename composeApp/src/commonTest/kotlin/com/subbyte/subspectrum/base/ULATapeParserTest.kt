package com.subbyte.subspectrum.base

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ULATapeParserTest {
    @Test
    fun parsesStandardSpeedDataBlock() {
        val payload = byteArrayOf(0x11, 0x22, 0x33)
        val rawSpectrumBlock = buildSpectrumTapeBlock(flag = 0xFF, payload = payload)
        val tzxBytes = buildTzxWithStandardBlocks(500 to rawSpectrumBlock)

        val tapeImage = ULATapeParser.parse(tzxBytes)

        assertEquals(1, tapeImage.blocks.size)
        val dataBlock = tapeImage.blocks[0]
        assertEquals(0xFFu.toUByte(), dataBlock.flag)
        assertContentEquals(payload, dataBlock.payload)
        assertTrue(dataBlock.checksumIsValid)
    }

    @Test
    fun rejectsInvalidTzxSignature() {
        val invalidBytes = byteArrayOf(0x00, 0x01, 0x02)

        assertFailsWith<IllegalArgumentException> {
            ULATapeParser.parse(invalidBytes)
        }
    }
}

private fun buildTzxWithStandardBlocks(vararg blocks: Pair<Int, ByteArray>): ByteArray {
    val bytes = mutableListOf<Byte>()
    bytes += byteArrayOf(
        'Z'.code.toByte(),
        'X'.code.toByte(),
        'T'.code.toByte(),
        'a'.code.toByte(),
        'p'.code.toByte(),
        'e'.code.toByte(),
        '!'.code.toByte(),
        0x1A,
        0x01,
        0x20,
    ).toList()

    for ((pauseMs, rawSpectrumBlock) in blocks) {
        bytes += 0x10.toByte()
        bytes += (pauseMs and 0xFF).toByte()
        bytes += ((pauseMs shr 8) and 0xFF).toByte()

        val length = rawSpectrumBlock.size
        bytes += (length and 0xFF).toByte()
        bytes += ((length shr 8) and 0xFF).toByte()
        bytes += rawSpectrumBlock.toList()
    }

    return bytes.toByteArray()
}

private fun buildSpectrumTapeBlock(flag: Int, payload: ByteArray): ByteArray {
    var checksum = flag and 0xFF
    for (byteValue in payload) {
        checksum = checksum xor (byteValue.toInt() and 0xFF)
    }

    val block = ByteArray(payload.size + 2)
    block[0] = (flag and 0xFF).toByte()
    payload.copyInto(block, destinationOffset = 1)
    block[block.lastIndex] = checksum.toByte()
    return block
}
