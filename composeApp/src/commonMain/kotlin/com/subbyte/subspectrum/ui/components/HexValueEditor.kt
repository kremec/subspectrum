package com.subbyte.subspectrum.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private const val HEX_DIGITS = "0123456789ABCDEF"

@Composable
fun HexValueEditor(
    value: String,
    digits: Int,
    onValueCommitted: (Int) -> Unit,
    enabled: Boolean = true,
    color: Color = Color.Black,
    allowedCharacters: String = HEX_DIGITS,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }

    var isEditing by remember { mutableStateOf(false) }
    var hasReceivedFocus by remember { mutableStateOf(false) }
    var draft by remember {
        mutableStateOf(value.toEditorValue())
    }

    val displayColor = if (enabled) color else color.copy(alpha = 0.5f)
    val hasValue = draft.text.isNotEmpty()
    val borderColor = when {
        !enabled -> Color(0xFFE4E4E4)
        isEditing && !hasValue -> Color(0xFFD32F2F)
        isEditing -> Color.Black
        else -> Color(0xFFD0D0D0)
    }
    val textStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Light,
        color = displayColor,
        textAlign = TextAlign.Center,
    )
    val editorWidth = (4 + digits * 12).dp
    val editorModifier = Modifier
        .width(editorWidth)
        .clip(RoundedCornerShape(4.dp))
        .background(Color.White)
        .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(4.dp))
        .padding(horizontal = 4.dp, vertical = 2.dp)

    fun resetDraft() {
        draft = value.toEditorValue()
    }

    fun stopEditing(commit: Boolean) {
        if (commit && hasValue) {
            onValueCommitted(draft.text.toInt(radix = 16))
        } else {
            resetDraft()
        }

        hasReceivedFocus = false
        isEditing = false
    }

    LaunchedEffect(value, isEditing) {
        if (!isEditing) {
            resetDraft()
        }
    }

    LaunchedEffect(enabled) {
        if (!enabled && isEditing) {
            stopEditing(commit = false)
        }
    }

    LaunchedEffect(isEditing) {
        if (isEditing) {
            hasReceivedFocus = false
            focusRequester.requestFocus()
        }
    }

    if (isEditing) {
        BasicTextField(
            value = draft,
            onValueChange = { nextValue ->
                draft = nextValue.filterHexInput(allowedCharacters, digits)
            },
            singleLine = true,
            textStyle = textStyle,
            cursorBrush = SolidColor(displayColor),
            modifier = editorModifier
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    when {
                        focusState.isFocused -> hasReceivedFocus = true
                        hasReceivedFocus -> stopEditing(commit = hasValue)
                    }
                }
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) {
                        return@onPreviewKeyEvent false
                    }

                    when (event.key) {
                        Key.Enter -> {
                            stopEditing(commit = hasValue)
                            focusManager.clearFocus(force = true)
                            true
                        }

                        Key.Escape -> {
                            stopEditing(commit = false)
                            focusManager.clearFocus(force = true)
                            true
                        }

                        else -> false
                    }
                }
        )
        return
    }

    Text(
        text = value,
        style = textStyle,
        modifier = editorModifier.clickable(
            enabled = enabled,
            interactionSource = interactionSource,
            indication = null
        ) {
            resetDraft()
            hasReceivedFocus = false
            isEditing = true
        }
    )
}

private fun String.toEditorValue(): TextFieldValue {
    return TextFieldValue(text = this, selection = TextRange(0, length))
}

private fun TextFieldValue.filterHexInput(
    allowedCharacters: String,
    maxLength: Int,
): TextFieldValue {
    val filteredText = text
        .uppercase()
        .filter { it in allowedCharacters }
        .take(maxLength)

    return TextFieldValue(
        text = filteredText,
        selection = TextRange(
            start = filteredSelectionIndex(selection.start, allowedCharacters, filteredText.length),
            end = filteredSelectionIndex(selection.end, allowedCharacters, filteredText.length),
        )
    )
}

private fun TextFieldValue.filteredSelectionIndex(
    originalIndex: Int,
    allowedCharacters: String,
    filteredLength: Int,
): Int {
    return text
        .take(originalIndex)
        .uppercase()
        .count { it in allowedCharacters }
        .coerceAtMost(filteredLength)
}
