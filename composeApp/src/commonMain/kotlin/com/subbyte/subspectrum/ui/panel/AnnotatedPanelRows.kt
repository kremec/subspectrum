package com.subbyte.subspectrum.ui.panel

import androidx.compose.foundation.lazy.LazyListState
import com.subbyte.subspectrum.base.annotations.MemoryAnnotation
import com.subbyte.subspectrum.base.annotations.MemoryRegionAnnotation
import com.subbyte.subspectrum.base.annotations.buildMemoryAnnotationsByAddress
import com.subbyte.subspectrum.base.annotations.buildRegionDelimitersByRow

fun buildAnnotationsByAddress(
    enabled: Boolean,
    addresses: Iterable<Int>,
): Map<Int, List<MemoryAnnotation>> {
    return if (enabled) buildMemoryAnnotationsByAddress(addresses) else emptyMap()
}

fun <T> buildDelimitersByAddress(
    enabled: Boolean,
    rows: List<T>,
    regions: List<MemoryRegionAnnotation>,
    rowStart: (T) -> Int,
    rowEndInclusive: (T) -> Int,
): Map<Int, List<MemoryRegionAnnotation>> {
    return if (enabled) {
        buildRegionDelimitersByRow(
            rows = rows,
            regionAnnotations = regions,
            rowStart = rowStart,
            rowEndInclusive = rowEndInclusive,
        )
    } else {
        emptyMap()
    }
}

fun <T> buildLazyItemIndexes(
    rows: List<T>,
    annotationsByAddress: Map<Int, List<*>>,
    delimiterMap: Map<Int, List<*>>,
    rowStart: (T) -> Int,
): Map<Int, Int> {
    val indexes = mutableMapOf<Int, Int>()
    var lazyItemIndex = 0

    rows.forEach { row ->
        val address = rowStart(row)

        if (delimiterMap[address].orEmpty().isNotEmpty()) {
            lazyItemIndex++
        }
        if (annotationsByAddress[address].orEmpty().isNotEmpty()) {
            lazyItemIndex++
        }

        indexes[address] = lazyItemIndex
        lazyItemIndex++
    }

    return indexes
}

suspend fun LazyListState.scrollToRowIfNeeded(
    rowIndex: Int,
    rowAddress: Int,
    lazyItemIndexesByAddress: Map<Int, Int>,
) {
    val lazyItemIndex = lazyItemIndexesByAddress[rowAddress] ?: rowIndex
    val visibleItems = layoutInfo.visibleItemsInfo
    val visibleIndex = visibleItems.indexOfFirst { it.index == lazyItemIndex }
    val comfortablyVisible = visibleIndex > 0 && visibleIndex < visibleItems.lastIndex

    if (!comfortablyVisible) {
        scrollToItem(lazyItemIndex)
    }
}

fun LazyListState.firstVisibleRowAddress(
    rowKeyPrefix: String,
    radix: Int = 10,
): Int? {
    return layoutInfo.visibleItemsInfo.firstNotNullOfOrNull { item ->
        val key = item.key as? String ?: return@firstNotNullOfOrNull null
        key.removePrefix(rowKeyPrefix)
            .takeIf { it != key }
            ?.toIntOrNull(radix)
    }
}
