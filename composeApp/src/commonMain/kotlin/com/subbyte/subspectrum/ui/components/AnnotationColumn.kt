package com.subbyte.subspectrum.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.subbyte.subspectrum.base.annotations.MemoryAnnotation

@Composable
fun AnnotationColumn(
    annotations: List<MemoryAnnotation>,
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        annotations.forEach { annotation ->
            if (annotation.description.isBlank()) {
                AnnotationText(annotation = annotation, color = color)
            } else {
                AnnotationTooltip(tooltip = annotation.description) {
                    AnnotationText(annotation = annotation, color = color)
                }
            }
        }
    }
}

@Composable
private fun AnnotationText(
    annotation: MemoryAnnotation,
    color: Color,
) {
    Text(
        text = annotation.label,
        color = color,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Light,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
