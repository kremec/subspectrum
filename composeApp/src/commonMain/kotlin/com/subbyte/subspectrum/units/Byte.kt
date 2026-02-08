package com.subbyte.subspectrum.units

fun Byte.getBit(position: Int): Boolean {
    return (this.toInt() and (1 shl position)) != 0
}

fun Byte.setBit(position: Int, value: Boolean): Byte {
    return if (value) {
        (this.toInt() or (1 shl position)).toByte()
    } else {
        (this.toInt() and (1 shl position).inv()).toByte()
    }
}

fun Byte.displayString(): String {
    return "${this.toHexString(HexFormat.UpperCase)}h"
}

fun Byte.displayStringWithSign(): String {
    val value = this.toInt()
    return if (value < 0) {
        val negValue = (-value).toByte()
        "-${negValue.toHexString(HexFormat.UpperCase)}h"
    } else {
        "+${this.toHexString(HexFormat.UpperCase)}h"
    }
}

fun Byte.displayStringDisplacement(): String {
    val value = this.toInt()
    return if (value < 0) {
        val negValue = (-value).toByte()
        "-${negValue.toHexString(HexFormat.UpperCase)}h"
    } else {
        "+${this.toHexString(HexFormat.UpperCase)}h"
    }
}

fun UByte.displayString(): String {
    return "${this.toHexString(HexFormat.UpperCase)}h"
}
