package com.subbyte.subspectrum.proc.instructions

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BitPatternTest {
    @Test
    fun matchesSimplePattern() {
        val pattern = BitPattern.of("01xxxyyy")
        // 01 101 010 = 0x6A
        assertTrue(pattern.matches(0x6A))
        // 01 000 111 = 0x47
        assertTrue(pattern.matches(0x47))
        // 00 101 010 = doesn't match (first bit wrong)
        assertFalse(pattern.matches(0x2A))
    }

    @Test
    fun extractSingleBitField() {
        val pattern = BitPattern.of("01xxxyyy")
        // 01 101 010 = 0x6A
        val word = 0x6AL
        assertEquals(0b101, pattern.get(word, 'x'))
        assertEquals(0b010, pattern.get(word, 'y'))
    }

    @Test
    fun extractMultiBytePattern() {
        val pattern = BitPattern.of("00xxx110 nnnnnnnn")
        // 00 101 110 = 0x2E, n = 0x34
        val word = 0x2E34L
        assertTrue(pattern.matches(word))
        assertEquals(0b101, pattern.get(word, 'x'))
        assertEquals(0x34, pattern.u8(word, 'n'))
    }

    @Test
    fun extractSignedByte() {
        val pattern = BitPattern.of("11011101 01rrr110 dddddddd")
        // DD, 56, FE (-2 as signed)
        val word = 0xDD56FEL
        assertTrue(pattern.matches(word))
        assertEquals(0b010, pattern.get(word, 'r'))
        assertEquals(-2, pattern.s8(word, 'd'))
    }

    @Test
    fun extractPositiveSignedByte() {
        val pattern = BitPattern.of("11011101 01rrr110 dddddddd")
        // DD, 56, 7F (+127)
        val word = 0xDD567FL
        assertEquals(127, pattern.s8(word, 'd'))
    }

    @Test
    fun patternWithWildcards() {
        val pattern = BitPattern.of("01...110")
        assertTrue(pattern.matches(0b01000110))
        assertTrue(pattern.matches(0b01111110))
        assertTrue(pattern.matches(0b01010110))
        assertFalse(pattern.matches(0b00000110))
    }

    @Test
    fun patternWithSpaces() {
        val pattern1 = BitPattern.of("01xxxyyy")
        val pattern2 = BitPattern.of("01xxx yyy")
        val word = 0x6AL
        assertEquals(pattern1.matches(word), pattern2.matches(word))
        assertEquals(pattern1.get(word, 'x'), pattern2.get(word, 'x'))
    }

    @Test
    fun byteCount() {
        assertEquals(1, BitPattern.of("01xxxyyy").byteCount)
        assertEquals(2, BitPattern.of("00xxx110 nnnnnnnn").byteCount)
        assertEquals(3, BitPattern.of("11011101 01rrr110 dddddddd").byteCount)
    }
}