package com.subbyte.subspectrum.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.subbyte.subspectrum.base.annotations.MemoryRegionAnnotation

@Composable
fun RegionDelimiterRow(
    region: MemoryRegionAnnotation,
    modifier: Modifier = Modifier,
) {
    Text(
        text = region.label,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF444444),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
    )
}
