package com.subbyte.subspectrum.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.subbyte.subspectrum.base.annotations.MemoryAnnotation
import com.subbyte.subspectrum.base.annotations.MemoryRegionAnnotation
import com.subbyte.subspectrum.base.annotations.activeRegionAt
import com.subbyte.subspectrum.base.annotations.stickyAnnotationsForAddress

@Composable
fun StickyAnnotationOverlay(
    enabled: Boolean,
    lazyListState: LazyListState,
    annotationsByAddress: Map<Int, List<MemoryAnnotation>>,
    regionAnnotations: List<MemoryRegionAnnotation>,
    annotationKeyPrefix: String,
    delimiterKeyPrefix: String,
    firstVisibleAddress: () -> Int?,
    modifier: Modifier = Modifier,
    top: Dp = 28.dp,
) {
    val topPx = with(LocalDensity.current) { top.roundToPx() }
    val stickyAnnotations by remember(enabled, annotationsByAddress, regionAnnotations, topPx) {
        derivedStateOf {
            if (!enabled) return@derivedStateOf emptyList()

            val firstVisibleAddress = firstVisibleAddress() ?: return@derivedStateOf emptyList()
            val incomingAnnotationAddress = lazyListState.layoutInfo.visibleItemsInfo
                .asSequence()
                .mapNotNull { item ->
                    val address = (item.key as? String)?.addressFromKey(annotationKeyPrefix)
                    if (address == null || item.offset <= topPx) null else address
                }
                .minOrNull()
            if (incomingAnnotationAddress != null) {
                val incomingRegion = activeRegionAt(incomingAnnotationAddress, regionAnnotations)
                val previousAnnotationAddress = annotationsByAddress.keys
                    .filter { address ->
                        address < incomingAnnotationAddress &&
                            activeRegionAt(address, regionAnnotations) == incomingRegion
                    }
                    .maxOrNull()
                    ?: return@derivedStateOf emptyList()
                return@derivedStateOf annotationsByAddress[previousAnnotationAddress].orEmpty()
            }

            stickyAnnotationsForAddress(
                address = firstVisibleAddress,
                annotationsByAddress = annotationsByAddress,
                regionAnnotations = regionAnnotations,
            )
        }
    }
    if (stickyAnnotations.isEmpty()) return

    var heightPx by remember { mutableIntStateOf(0) }
    val offsetPx by remember(annotationKeyPrefix, delimiterKeyPrefix, topPx, heightPx) {
        derivedStateOf {
            if (heightPx == 0) return@derivedStateOf 0

            val overlayBottom = topPx + heightPx
            val nextHeaderTop = lazyListState.layoutInfo.visibleItemsInfo
                .asSequence()
                .mapNotNull { item ->
                    val key = item.key as? String ?: return@mapNotNull null
                    if (
                        (key.startsWith(annotationKeyPrefix) && item.offset > topPx) ||
                        (key.startsWith(delimiterKeyPrefix) && item.offset > 0)
                    ) {
                        item.offset
                    } else {
                        null
                    }
                }
                .minOrNull()
                ?: return@derivedStateOf 0

            minOf(0, nextHeaderTop - overlayBottom)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = top)
            .clipToBounds(),
    ) {
        AnnotationColumn(
            annotations = stickyAnnotations,
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(x = 0, y = offsetPx) }
                .onSizeChanged { heightPx = it.height }
                .background(Color.White)
                .padding(horizontal = 4.dp, vertical = 4.dp),
        )
    }
}

private fun String.addressFromKey(prefix: String): Int? {
    val value = removePrefix(prefix).takeIf { it != this } ?: return null
    return value.toIntOrNull() ?: value.takeIf { it.length == 4 }?.toIntOrNull(16)
}
