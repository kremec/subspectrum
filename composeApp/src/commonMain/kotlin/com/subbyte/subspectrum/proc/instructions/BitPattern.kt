data class BitPattern private constructor(
    val text: String,
    val bitCount: Int,
    val byteCount: Int,
    private val mask: Long,
    private val value: Long,
    private val fields: Map<Char, IntArray>, // bit positions (LSB=0), stored in parse order
) {
    fun matches(word: Long): Boolean = (word and mask) == value

    /**
     * Extract captured bits for [name] in the same left-to-right order
     * as they appear in the pattern.
     */
    fun get(word: Long, name: Char): Int {
        val positions =
            fields[name] ?: error("Field '$name' not present in pattern: $text")

        var out = 0
        for (p in positions) {
            out = (out shl 1) or (((word ushr p) and 1L).toInt())
        }
        return out
    }

    fun u8(word: Long, name: Char): Int = get(word, name) and 0xFF

    fun s8(word: Long, name: Char): Int {
        val v = u8(word, name)
        return if (v and 0x80 != 0) v - 0x100 else v
    }

    companion object Companion {
        /**
         * Pattern syntax:
         * - '0'/'1' are fixed bits
         * - '.' is a wildcard
         * - any other char (e.g. x,y,r,n,d) captures that bit into a named field
         * - spaces/underscores are ignored
         *
         * Pattern is written MSB -> LSB (left -> right), optionally spanning bytes.
         */
        fun of(pattern: String): BitPattern {
            val cleaned = pattern.filterNot { it == ' ' || it == '_' }
            require(cleaned.isNotEmpty()) { "Empty pattern" }
            require(cleaned.length <= 64) {
                "Pattern too long (>64 bits): $pattern"
            }

            val bitCount = cleaned.length
            val byteCount = (bitCount + 7) / 8

            var mask = 0L
            var value = 0L
            val tmpFields = linkedMapOf<Char, MutableList<Int>>()

            // cleaned is MSB->LSB; internal bit positions are LSB=0
            for ((i, ch) in cleaned.withIndex()) {
                val bitPos = bitCount - 1 - i
                when (ch) {
                    '0' -> mask = mask or (1L shl bitPos)
                    '1' -> {
                        mask = mask or (1L shl bitPos)
                        value = value or (1L shl bitPos)
                    }
                    '.' -> Unit
                    else -> tmpFields.getOrPut(ch) { mutableListOf() }.add(bitPos)
                }
            }

            val fields = tmpFields.mapValues { (_, v) -> v.toIntArray() }

            return BitPattern(
                text = pattern,
                bitCount = bitCount,
                byteCount = byteCount,
                mask = mask,
                value = value,
                fields = fields,
            )
        }
    }
}