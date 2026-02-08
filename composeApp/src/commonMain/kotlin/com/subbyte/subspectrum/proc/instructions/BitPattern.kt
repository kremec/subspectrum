import com.subbyte.subspectrum.base.*
import com.subbyte.subspectrum.units.DataByteArray
import com.subbyte.subspectrum.units.UWord
import com.subbyte.subspectrum.units.uWordFromBytes

data class BitPattern constructor(
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
    private fun get(word: Long, name: Char): Int {
        val positions =
            fields[name] ?: error("Field '$name' not present in pattern: $text")

        var out = 0
        for (p in positions) {
            out = (out shl 1) or (((word ushr p) and 1L).toInt())
        }
        return out
    }

    fun getRegisterCode(word: Long, name: Char): RegisterCode {
        val bitValue = get(word, name)
        return RegisterCode.entries.first { it.code == bitValue }
    }

    fun getRegisterPairSSCode(word: Long, name: Char): RegisterPairSSCode {
        val bitValue = get(word, name)
        return RegisterPairSSCode.entries.first { it.code == bitValue }
    }
    fun getRegisterPairQQCode(word: Long, name: Char): RegisterPairQQCode {
        val bitValue = get(word, name)
        return RegisterPairQQCode.entries.first { it.code == bitValue }
    }
    fun getRegisterPairPPCode(word: Long, name: Char): RegisterPairPPCode {
        val bitValue = get(word, name)
        return RegisterPairPPCode.entries.first { it.code == bitValue }
    }
    fun getRegisterPairRRCode(word: Long, name: Char): RegisterPairRRCode {
        val bitValue = get(word, name)
        return RegisterPairRRCode.entries.first { it.code == bitValue }
    }

    fun getConditionCode(word: Long, name: Char): ConditionCode {
        val bitValue = get(word, name)
        return ConditionCode.entries.first { it.code == bitValue }
    }

    fun getByte(word: Long, name: Char): Byte {
        val bitValue = get(word, name)
        return bitValue.toByte()
    }
    fun getUByte(word: Long, name: Char): UByte {
        val bitValue = get(word, name)
        return bitValue.toUByte()
    }

    fun getUWord(word: Long, lowName: Char, highName: Char): UWord {
        val bitLowValue = get(word, lowName).toByte()
        val bitHighValue = get(word, highName).toByte()
        return Pair(bitHighValue, bitLowValue).uWordFromBytes()
    }

    fun getBitPosition(word: Long, name: Char): Int {
        return get(word, name)
    }

    fun getRSTOffset(word: Long, name: Char): UByte {
        val bitValue = get(word, name)
        return (bitValue shl 3).toUByte()
    }

    fun getOpcodeFetchCount(): Int {
        val cleaned = text.filterNot { it == ' ' || it == '_' }
        val bytes = cleaned.chunked(8)
        var count = 0
        for (byte in bytes) {
            val first = byte.firstOrNull() ?: continue
            val isImmediateByte = byte.all { it == first && it != '0' && it != '1' && it != '.' }
            if (!isImmediateByte) {
                count++
            }
        }
        return count
    }

    fun toInstructionByteArray(word: Long): DataByteArray {
        val byteArray = ByteArray(byteCount) { i ->
            val shift = 8 * (byteCount - 1 - i)
            ((word shr shift) and 0xFF).toByte()
        }
        return DataByteArray(byteArray)
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
