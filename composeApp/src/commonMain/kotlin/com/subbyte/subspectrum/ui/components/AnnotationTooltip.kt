package com.subbyte.subspectrum.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnotationTooltip(
    tooltip: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    TooltipBox(
        tooltip = {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text(
                    text = tooltip,
                    style = TextStyle(color = Color.Black),
                    modifier = Modifier
                        .widthIn(max = 320.dp)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                )
            }
        },
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        state = rememberTooltipState(isPersistent = true),
        modifier = modifier,
        content = content,
    )
}
