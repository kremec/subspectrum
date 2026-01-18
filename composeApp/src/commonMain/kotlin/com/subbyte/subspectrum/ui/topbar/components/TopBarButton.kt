package com.subbyte.subspectrum.ui.topbar.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarButton(
    onClick: () -> Unit,
    tooltip: String,
    content: @Composable (() -> Unit)
) {
    TooltipBox(
        tooltip = {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text(text = tooltip, modifier = Modifier.padding(horizontal = 6.dp),)
            }
        },
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        state = rememberTooltipState()
    ) {
        IconButton(
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.size(30.dp),
            onClick = onClick
        ) {
            content()
        }
    }
}