package com.subbyte.subspectrum.base

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import com.subbyte.subspectrum.units.toBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ULAKeyboardInputMode {
    Authentic,
    Actual,
}

internal object ULAKeyboard {
    private const val CHARACTER_KEY_PULSE_MILLIS = 30L

    private const val ULA_IO_PORT_LOW_BYTE = 0xFE.toByte()

    private const val KEYBOARD_ROW_COUNT = 8
    private const val KEYBOARD_COLUMN_COUNT = 5
    private const val KEYBOARD_ROW_IDLE = (1 shl KEYBOARD_COLUMN_COUNT) - 1
    private const val KEYBOARD_PORT_HIGH_BITS = 0b1010_0000

    private data class MatrixKey(
        val row: Int,
        val bit: Int,
    )

    private val keyboardRows = IntArray(KEYBOARD_ROW_COUNT) { KEYBOARD_ROW_IDLE }
    private val hostLayoutPressedKeyMappings = mutableMapOf<Key, List<MatrixKey>>()

    var keyboardInputMode: MutableState<ULAKeyboardInputMode> =
        mutableStateOf(ULAKeyboardInputMode.Authentic)

    fun getKeyboardPortValue(portAddress: UShort): Byte? {
        if (portAddress.toBytes().second != ULA_IO_PORT_LOW_BYTE) {
            return null
        }

        val rowSelectMask = (portAddress.toInt() shr 8) and 0x00FF
        var keyBits = KEYBOARD_ROW_IDLE
        for (row in 0 until KEYBOARD_ROW_COUNT) {
            val rowSelected = ((rowSelectMask shr row) and 0x01) == 0
            if (rowSelected) {
                keyBits = keyBits and keyboardRows[row]
            }
        }

        return (KEYBOARD_PORT_HIGH_BITS or keyBits).toByte()
    }

    fun setKeyboardKeyState(key: Key, isPressed: Boolean, inputChar: Char?): Boolean {
        return when (keyboardInputMode.value) {
            ULAKeyboardInputMode.Authentic -> setKeyboardKeyStateAuthentic(key, isPressed)
            ULAKeyboardInputMode.Actual -> setKeyboardKeyStateHostLayout(key, isPressed, inputChar)
        }
    }

    fun setKeyboardCharacterState(inputChar: Char, isPressed: Boolean): Boolean {
        if (keyboardInputMode.value != ULAKeyboardInputMode.Actual) {
            return false
        }

        val mappedKeys = getMatrixKeysForCharacter(inputChar) ?: return false
        applyMatrixKeyState(mappedKeys, isPressed)
        return true
    }

    fun handlePreviewKeyEvent(event: KeyEvent, keyPulseScope: CoroutineScope): Boolean {
        val inputChar = event
            .utf16CodePoint
            .takeIf { codePoint -> codePoint > 0 }
            ?.toChar()
            ?.takeIf { char -> !char.isISOControl() }

        return when (event.type) {
            KeyEventType.KeyDown -> setKeyboardKeyState(event.key, isPressed = true, inputChar = inputChar)
            KeyEventType.KeyUp -> setKeyboardKeyState(event.key, isPressed = false, inputChar = inputChar)
            KeyEventType.Unknown -> {
                if (inputChar == null) {
                    false
                } else {
                    val pressed = setKeyboardCharacterState(inputChar, isPressed = true)
                    if (pressed) {
                        keyPulseScope.launch {
                            delay(CHARACTER_KEY_PULSE_MILLIS)
                            setKeyboardCharacterState(inputChar, isPressed = false)
                        }
                    }
                    pressed
                }
            }
            else -> false
        }
    }

    fun toggleKeyboardInputMode() {
        keyboardInputMode.value = when (keyboardInputMode.value) {
            ULAKeyboardInputMode.Authentic -> ULAKeyboardInputMode.Actual
            ULAKeyboardInputMode.Actual -> ULAKeyboardInputMode.Authentic
        }
        releaseAllKeyboardKeys()
    }

    fun releaseAllKeyboardKeys() {
        for (row in keyboardRows.indices) {
            keyboardRows[row] = KEYBOARD_ROW_IDLE
        }
        hostLayoutPressedKeyMappings.clear()
    }

    private fun setKeyboardKeyStateAuthentic(key: Key, isPressed: Boolean): Boolean {
        val mappedKeys = getMatrixKeysForKey(key) ?: return false

        applyMatrixKeyState(mappedKeys, isPressed)

        return true
    }

    private fun setKeyboardKeyStateHostLayout(key: Key, isPressed: Boolean, inputChar: Char?): Boolean {
        if (isPressed) {
            val mappedKeys = getHostLayoutMatrixKeys(key, inputChar) ?: return false
            hostLayoutPressedKeyMappings[key] = mappedKeys
            applyMatrixKeyState(mappedKeys, isPressed = true)
            return true
        }

        val mappedKeys = hostLayoutPressedKeyMappings.remove(key)
            ?: getHostLayoutMatrixKeys(key, inputChar)
            ?: return false

        applyMatrixKeyState(mappedKeys, isPressed = false)

        return true
    }

    private fun applyMatrixKeyState(mappedKeys: List<MatrixKey>, isPressed: Boolean) {
        for (mappedKey in mappedKeys) {
            setMatrixKeyState(
                row = mappedKey.row,
                bit = mappedKey.bit,
                isPressed = isPressed,
            )
        }
    }

    private fun getHostLayoutMatrixKeys(key: Key, inputChar: Char?): List<MatrixKey>? {
        if (inputChar != null && !inputChar.isISOControl()) {
            return getMatrixKeysForCharacter(inputChar)
        }

        return when (key) {
            Key.ShiftLeft,
            Key.ShiftRight,
            Key.CtrlLeft,
            Key.CtrlRight,
            -> null

            Key.Enter,
            Key.Spacebar,
            Key.DirectionLeft,
            Key.DirectionDown,
            Key.DirectionUp,
            Key.DirectionRight,
            Key.Backspace,
            -> getMatrixKeysForKey(key)

            else -> null
        }
    }

    private fun getMatrixKeysForCharacter(inputChar: Char): List<MatrixKey>? {
        if (inputChar in 'a'..'z') {
            return getMatrixKeyForLowercaseLetter(inputChar)?.let { listOf(it) }
        }

        if (inputChar in 'A'..'Z') {
            val lowercaseMapping = getMatrixKeyForLowercaseLetter(inputChar.lowercaseChar()) ?: return null
            return listOf(MatrixKey(0, 0), lowercaseMapping)
        }

        return when (inputChar) {
            '0' -> listOf(MatrixKey(4, 0))
            '1' -> listOf(MatrixKey(3, 0))
            '2' -> listOf(MatrixKey(3, 1))
            '3' -> listOf(MatrixKey(3, 2))
            '4' -> listOf(MatrixKey(3, 3))
            '5' -> listOf(MatrixKey(3, 4))
            '6' -> listOf(MatrixKey(4, 4))
            '7' -> listOf(MatrixKey(4, 3))
            '8' -> listOf(MatrixKey(4, 2))
            '9' -> listOf(MatrixKey(4, 1))

            ' ' -> listOf(MatrixKey(7, 0))

            '!' -> listOf(MatrixKey(7, 1), MatrixKey(3, 0))
            '@' -> listOf(MatrixKey(7, 1), MatrixKey(3, 1))
            '#' -> listOf(MatrixKey(7, 1), MatrixKey(3, 2))
            '$' -> listOf(MatrixKey(7, 1), MatrixKey(3, 3))
            '%' -> listOf(MatrixKey(7, 1), MatrixKey(3, 4))
            '&' -> listOf(MatrixKey(7, 1), MatrixKey(4, 4))
            '\'' -> listOf(MatrixKey(7, 1), MatrixKey(4, 3))
            '(' -> listOf(MatrixKey(7, 1), MatrixKey(4, 2))
            ')' -> listOf(MatrixKey(7, 1), MatrixKey(4, 1))
            '_' -> listOf(MatrixKey(7, 1), MatrixKey(4, 0))

            '"' -> listOf(MatrixKey(7, 1), MatrixKey(5, 0))
            ';' -> listOf(MatrixKey(7, 1), MatrixKey(5, 1))
            ':' -> listOf(MatrixKey(7, 1), MatrixKey(0, 1))
            ',' -> listOf(MatrixKey(7, 1), MatrixKey(7, 3))
            '.' -> listOf(MatrixKey(7, 1), MatrixKey(7, 2))
            '/' -> listOf(MatrixKey(7, 1), MatrixKey(0, 4))
            '?' -> listOf(MatrixKey(7, 1), MatrixKey(0, 3))
            '+' -> listOf(MatrixKey(7, 1), MatrixKey(6, 2))
            '-' -> listOf(MatrixKey(7, 1), MatrixKey(6, 3))
            '=' -> listOf(MatrixKey(7, 1), MatrixKey(6, 1))
            '*' -> listOf(MatrixKey(7, 1), MatrixKey(7, 4))
            '<' -> listOf(MatrixKey(7, 1), MatrixKey(2, 3))
            '>' -> listOf(MatrixKey(7, 1), MatrixKey(2, 4))
            '[' -> listOf(MatrixKey(7, 1), MatrixKey(5, 4))
            ']' -> listOf(MatrixKey(7, 1), MatrixKey(5, 3))
            else -> null
        }
    }

    private fun getMatrixKeyForLowercaseLetter(char: Char): MatrixKey? {
        return when (char) {
            'a' -> MatrixKey(1, 0)
            'b' -> MatrixKey(7, 4)
            'c' -> MatrixKey(0, 3)
            'd' -> MatrixKey(1, 2)
            'e' -> MatrixKey(2, 2)
            'f' -> MatrixKey(1, 3)
            'g' -> MatrixKey(1, 4)
            'h' -> MatrixKey(6, 4)
            'i' -> MatrixKey(5, 2)
            'j' -> MatrixKey(6, 3)
            'k' -> MatrixKey(6, 2)
            'l' -> MatrixKey(6, 1)
            'm' -> MatrixKey(7, 2)
            'n' -> MatrixKey(7, 3)
            'o' -> MatrixKey(5, 1)
            'p' -> MatrixKey(5, 0)
            'q' -> MatrixKey(2, 0)
            'r' -> MatrixKey(2, 3)
            's' -> MatrixKey(1, 1)
            't' -> MatrixKey(2, 4)
            'u' -> MatrixKey(5, 3)
            'v' -> MatrixKey(0, 4)
            'w' -> MatrixKey(2, 1)
            'x' -> MatrixKey(0, 2)
            'y' -> MatrixKey(5, 4)
            'z' -> MatrixKey(0, 1)
            else -> null
        }
    }

    private fun setMatrixKeyState(row: Int, bit: Int, isPressed: Boolean) {
        val keyMask = 1 shl bit
        keyboardRows[row] = if (isPressed) {
            keyboardRows[row] and keyMask.inv()
        } else {
            keyboardRows[row] or keyMask
        }
    }

    private fun getMatrixKeysForKey(key: Key): List<MatrixKey>? {
        return when (key) {
            Key.ShiftLeft,
            Key.ShiftRight,
            -> listOf(MatrixKey(0, 0))

            Key.Z -> listOf(MatrixKey(0, 1))
            Key.X -> listOf(MatrixKey(0, 2))
            Key.C -> listOf(MatrixKey(0, 3))
            Key.V -> listOf(MatrixKey(0, 4))

            Key.A -> listOf(MatrixKey(1, 0))
            Key.S -> listOf(MatrixKey(1, 1))
            Key.D -> listOf(MatrixKey(1, 2))
            Key.F -> listOf(MatrixKey(1, 3))
            Key.G -> listOf(MatrixKey(1, 4))

            Key.Q -> listOf(MatrixKey(2, 0))
            Key.W -> listOf(MatrixKey(2, 1))
            Key.E -> listOf(MatrixKey(2, 2))
            Key.R -> listOf(MatrixKey(2, 3))
            Key.T -> listOf(MatrixKey(2, 4))

            Key.One -> listOf(MatrixKey(3, 0))
            Key.Two -> listOf(MatrixKey(3, 1))
            Key.Three -> listOf(MatrixKey(3, 2))
            Key.Four -> listOf(MatrixKey(3, 3))
            Key.Five -> listOf(MatrixKey(3, 4))

            Key.Zero -> listOf(MatrixKey(4, 0))
            Key.Nine -> listOf(MatrixKey(4, 1))
            Key.Eight -> listOf(MatrixKey(4, 2))
            Key.Seven -> listOf(MatrixKey(4, 3))
            Key.Six -> listOf(MatrixKey(4, 4))

            Key.P -> listOf(MatrixKey(5, 0))
            Key.O -> listOf(MatrixKey(5, 1))
            Key.I -> listOf(MatrixKey(5, 2))
            Key.U -> listOf(MatrixKey(5, 3))
            Key.Y -> listOf(MatrixKey(5, 4))

            Key.Enter -> listOf(MatrixKey(6, 0))
            Key.L -> listOf(MatrixKey(6, 1))
            Key.K -> listOf(MatrixKey(6, 2))
            Key.J -> listOf(MatrixKey(6, 3))
            Key.H -> listOf(MatrixKey(6, 4))

            Key.Spacebar -> listOf(MatrixKey(7, 0))

            Key.CtrlLeft,
            Key.CtrlRight,
            -> listOf(MatrixKey(7, 1))

            Key.M -> listOf(MatrixKey(7, 2))
            Key.N -> listOf(MatrixKey(7, 3))
            Key.B -> listOf(MatrixKey(7, 4))

            Key.DirectionLeft -> listOf(
                MatrixKey(0, 0),
                MatrixKey(3, 4),
            )

            Key.DirectionDown -> listOf(
                MatrixKey(0, 0),
                MatrixKey(4, 4),
            )

            Key.DirectionUp -> listOf(
                MatrixKey(0, 0),
                MatrixKey(4, 3),
            )

            Key.DirectionRight -> listOf(
                MatrixKey(0, 0),
                MatrixKey(4, 2),
            )

            Key.Backspace -> listOf(
                MatrixKey(0, 0),
                MatrixKey(4, 0),
            )

            else -> null
        }
    }
}
