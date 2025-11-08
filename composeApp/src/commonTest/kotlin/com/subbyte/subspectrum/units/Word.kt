package com.subbyte.subspectrum.units

import kotlin.test.Test
import kotlin.test.assertEquals

class WordTest {
    @Test
    fun wordFromBytes() {
        val bytes: Pair<Byte, Byte> = Pair(0x12, 0x34)
        val word: Word = bytes.fromBytes()
        assertEquals(word, 0x1234)
    }

    @Test
    fun bytesFromWord() {
        val word: Word = 0x1234
        val bytes: Pair<Byte, Byte> = word.toBytes()
        assertEquals(bytes, Pair<Byte, Byte>(0x12, 0x34))
    }
}