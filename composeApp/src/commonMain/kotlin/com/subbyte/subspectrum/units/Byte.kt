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