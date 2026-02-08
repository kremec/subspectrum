package com.subbyte.subspectrum.units

typealias Word = Short
typealias UWord = UShort

fun Pair<Byte, Byte>.wordFromBytes(): Word {
    return ((first.toInt() and 0xFF) shl 8 or (second.toInt() and 0xFF)).toShort()
}
fun Pair<Byte, Byte>.uWordFromBytes(): UWord {
    return ((first.toInt() and 0xFF) shl 8 or (second.toInt() and 0xFF)).toUShort()
}

fun Word.toBytes(): Pair<Byte, Byte> {
    val intValue = this.toInt() and 0xFFFF
    val highByte = (intValue shr 8).toByte()
    val lowByte = intValue.toByte()
    return Pair(highByte, lowByte)
}
fun UWord.toBytes(): Pair<Byte, Byte> {
    val intValue = this.toInt() and 0xFFFF
    val highByte = (intValue shr 8).toByte()
    val lowByte = intValue.toByte()
    return Pair(highByte, lowByte)
}

fun Word.displayString(): String {
    return "${this.toHexString(HexFormat.UpperCase)}h"
}
fun UWord.displayString(): String {
    return "${this.toHexString(HexFormat.UpperCase)}h"
}
