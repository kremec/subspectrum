package com.subbyte.subspectrum.units

data class DataByteArray(private val bytes: ByteArray) : Iterable<Byte> {

    val size: Int
        get() = bytes.size

    operator fun get(index: Int): Byte = bytes[index]

    operator fun set(index: Int, value: Byte) {
        bytes[index] = value
    }

    fun sliceArray(range: IntRange): ByteArray = bytes.sliceArray(range)

    fun copyInto(
        destination: DataByteArray,
        destinationOffset: Int = 0,
        startIndex: Int = 0,
        endIndex: Int = bytes.size
    ): DataByteArray = bytes.copyInto(destination.bytes, destinationOffset, startIndex, endIndex).let { destination }

    fun fill(value: Byte, fromIndex: Int = 0, toIndex: Int = bytes.size) {
        bytes.fill(value, fromIndex, toIndex)
    }

    override fun iterator(): Iterator<Byte> = bytes.iterator()

    fun joinToString(
        separator: String = ", ",
        prefix: String = "",
        postfix: String = "",
        limit: Int = -1,
        truncated: String = "...",
        transform: ((Byte) -> CharSequence)? = null
    ): String = bytes.joinToString(separator, prefix, postfix, limit, truncated, transform)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DataByteArray) return false
        return bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int = bytes.contentHashCode()

    override fun toString(): String =
        bytes.joinToString(prefix = "[", postfix = "]") { byte ->
            "0x" + (byte.toInt() and 0xFF).toString(16).padStart(2, '0').uppercase()
        }
}
