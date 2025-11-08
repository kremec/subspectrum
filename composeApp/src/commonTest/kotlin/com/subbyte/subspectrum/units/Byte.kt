package com.subbyte.subspectrum.units

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ByteTest {
    @Test
    fun bitFromByte() {
        val byte: Byte = 0b1010
        val bits = BooleanArray(4) { position -> byte.getBit(position) }
        assertContentEquals(booleanArrayOf(false, true, false, true), bits)
    }

    @Test
    fun setBit() {
        val byte = 0b1010.toByte()

        val setTo1 = byte.setBit(0, true)
        assertEquals(0b1011.toByte(), setTo1)

        val setTo0 = byte.setBit(1, false)
        assertEquals(0b1000.toByte(), setTo0)
    }
}