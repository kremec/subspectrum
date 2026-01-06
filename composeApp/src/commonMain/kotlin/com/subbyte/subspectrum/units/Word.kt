package com.subbyte.subspectrum.units

typealias Word = Short

fun Pair<Byte, Byte>.fromBytes(): Word {
    return ((first.toInt() and 0xFF) shl 8 or (second.toInt() and 0xFF)).toShort()
}

fun Word.toBytes(): Pair<Byte, Byte> {
    val intValue = this.toInt() and 0xFFFF
    val highByte = (intValue shr 8).toByte()
    val lowByte = intValue.toByte()
    return Pair(highByte, lowByte)
}