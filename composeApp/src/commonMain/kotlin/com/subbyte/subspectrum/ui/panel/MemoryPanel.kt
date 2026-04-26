package com.subbyte.subspectrum.ui.panel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationSearching
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.SpeakerNotes
import androidx.compose.material.icons.outlined.SpeakerNotesOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.subbyte.subspectrum.base.Address
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.base.annotations.MemoryAnnotation
import com.subbyte.subspectrum.base.annotations.MemoryRegionAnnotation
import com.subbyte.subspectrum.base.annotations.RAMSectionRegistry
import com.subbyte.subspectrum.base.annotations.buildAnnotationSplitAddresses
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.ui.components.AnnotationColumn
import com.subbyte.subspectrum.ui.components.IconButton
import com.subbyte.subspectrum.ui.components.HexValueEditor
import com.subbyte.subspectrum.ui.components.RegionDelimiterRow
import com.subbyte.subspectrum.ui.components.StickyAnnotationOverlay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private data class MemoryRow(
    val address: String,
    val startAddress: Int,
    val byteCount: Int,
) {
    val endAddress: Int
        get() = startAddress + byteCount - 1

    fun containsAddress(address: Address): Boolean {
        val start = startAddress.toUInt()
        val endExclusive = start + byteCount.toUInt()
        val value = address.toUInt()
        return value in start until endExclusive
    }
}

private data class MemoryLayout(
    val rows: List<MemoryRow>,
    val annotationsByAddress: Map<Int, List<MemoryAnnotation>>,
    val delimitersByAddress: Map<Int, List<MemoryRegionAnnotation>>,
    val lazyItemIndexesByAddress: Map<Int, Int>,
)

@Suppress("DEPRECATION")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MemoryPanel() {
    var renderVersion by remember { mutableIntStateOf(0) }
    var trackPc by remember { mutableStateOf(false) }
    var annotatedLayoutEnabled by remember { mutableStateOf(false) }
    val editingEnabled = !Processor.running.value

    LaunchedEffect(Unit) {
        var renderDirty = true

        launch {
            Memory.memorySet.invalidations.conflate().collect {
                renderDirty = true
            }
        }

        launch {
            Registers.specialPurposeRegisters.pcInvalidations.conflate().collect {
                renderDirty = true
            }
        }

        while (true) {
            withFrameNanos { }
            if (renderDirty) {
                renderDirty = false
                renderVersion++
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Memory")
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { annotatedLayoutEnabled = !annotatedLayoutEnabled },
                tooltip = if (annotatedLayoutEnabled) "Disable annotated layout" else "Enable annotated layout",
            ) {
                Icon(
                    imageVector = if (annotatedLayoutEnabled) {
                        Icons.Outlined.SpeakerNotes
                    } else {
                        Icons.Outlined.SpeakerNotesOff
                    },
                    contentDescription = if (annotatedLayoutEnabled) "Disable annotated layout" else "Enable annotated layout",
                    tint = if (annotatedLayoutEnabled) Color.Black else Color.Gray,
                )
            }
            IconButton(
                onClick = { trackPc = !trackPc },
                tooltip = if (trackPc) "Disable PC tracking" else "Enable PC tracking"
            ) {
                Icon(
                    imageVector = if (trackPc) Icons.Outlined.MyLocation else Icons.Outlined.LocationSearching,
                    contentDescription = if (trackPc) "PC tracking enabled" else "PC tracking disabled",
                    tint = if (trackPc) Color.Black else Color.Gray,
                )
            }
        }
        HorizontalDivider()

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val bytesPerRow = when {
                maxWidth < 300.dp -> 4
                maxWidth < 550.dp -> 8
                else -> 16
            }
            val regions = remember(renderVersion) {
                RAMSectionRegistry.allRegions(Memory.memorySet)
            }
            var rows by remember { mutableStateOf<List<MemoryRow>>(emptyList()) }
            LaunchedEffect(bytesPerRow, annotatedLayoutEnabled, regions) {
                rows = withContext(Dispatchers.Default) {
                    buildMemoryRows(
                        bytesPerRow = bytesPerRow,
                        annotatedLayoutEnabled = annotatedLayoutEnabled,
                        regions = regions,
                    )
                }
            }
            val layout = remember(rows, regions, annotatedLayoutEnabled) {
                buildMemoryLayout(
                    rows = rows,
                    regions = regions,
                    annotatedLayoutEnabled = annotatedLayoutEnabled,
                )
            }

            val lazyListState = rememberLazyListState()
            val pcAddress = Registers.specialPurposeRegisters.getPC().toUShort()
            val prevBytesPerRow = remember { mutableStateOf(bytesPerRow) }

            LaunchedEffect(renderVersion, trackPc) {
                if (!trackPc) return@LaunchedEffect

                val pcRowIndex = layout.rows.indexOfFirst { it.containsAddress(pcAddress) }
                if (pcRowIndex == -1) return@LaunchedEffect

                lazyListState.scrollToRowIfNeeded(
                    rowIndex = pcRowIndex,
                    rowAddress = layout.rows[pcRowIndex].startAddress,
                    lazyItemIndexesByAddress = layout.lazyItemIndexesByAddress,
                )
            }

            // When bytesPerRow changes, scroll to maintain the same address
            LaunchedEffect(bytesPerRow, annotatedLayoutEnabled, layout) {
                if (layout.rows.isEmpty()) return@LaunchedEffect

                if (bytesPerRow != prevBytesPerRow.value) {
                    val previousAddress = lazyListState.firstVisibleRowAddress("memory-row-", radix = 16)
                        ?: (lazyListState.firstVisibleItemIndex * prevBytesPerRow.value)
                    val newRowIndex = layout.rows.indexOfFirst {
                        previousAddress in it.startAddress..it.endAddress
                    }.takeIf { it >= 0 } ?: 0
                    val newLazyItemIndex = layout.lazyItemIndexesByAddress[layout.rows[newRowIndex].startAddress]
                        ?: newRowIndex
                    lazyListState.scrollToItem(newLazyItemIndex)
                    prevBytesPerRow.value = bytesPerRow
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(state = lazyListState) {
                    layout.rows.forEach { row ->
                        val rowAnnotations = layout.annotationsByAddress[row.startAddress].orEmpty()
                        val rowDelimiters = layout.delimitersByAddress[row.startAddress].orEmpty()

                        if (annotatedLayoutEnabled && rowDelimiters.isNotEmpty()) {
                            stickyHeader(key = "memory-delimiters-${row.address}") {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White),
                                ) {
                                    rowDelimiters.forEach { region ->
                                        RegionDelimiterRow(region = region)
                                    }
                                }
                            }
                        }

                        if (annotatedLayoutEnabled && rowAnnotations.isNotEmpty()) {
                            item(key = "memory-annotation-${row.address}") {
                                AnnotationColumn(
                                    annotations = rowAnnotations,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White)
                                        .padding(horizontal = 4.dp, vertical = 4.dp),
                                )
                            }
                        }

                        item(key = "memory-row-${row.address}") {
                            val rowContainsPc = pcAddress.toInt() in row.startAddress..row.endAddress
                            val rowBytes = remember(row.startAddress, row.byteCount, renderVersion) {
                                List(row.byteCount) { index ->
                                    val byteAddress = (row.startAddress + index).toUShort()
                                    byteAddress to Memory.memorySet.getMemoryCell(byteAddress)
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    row.address,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Light,
                                    color = if (rowContainsPc) Color.Red else Color.Black,
                                    modifier = Modifier.width(if (annotatedLayoutEnabled) 70.dp else 60.dp)
                                )
                                Row(
                                    modifier = Modifier,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    rowBytes.forEach { (byteAddress, byteValue) ->
                                        val textColor =
                                            if (byteAddress == pcAddress) Color.Red else Color.Black
                                        HexValueEditor(
                                            value = byteValue
                                                .toUByte()
                                                .toString(16)
                                                .padStart(2, '0')
                                                .uppercase(),
                                            digits = 2,
                                            color = textColor,
                                            enabled = editingEnabled,
                                            onValueCommitted = {
                                                Memory.memorySet.setMemoryCell(
                                                    byteAddress,
                                                    it.toByte(),
                                                    canOverwriteROM = true
                                                )
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                StickyAnnotationOverlay(
                    enabled = annotatedLayoutEnabled,
                    lazyListState = lazyListState,
                    annotationsByAddress = layout.annotationsByAddress,
                    regionAnnotations = regions,
                    annotationKeyPrefix = "memory-annotation-",
                    delimiterKeyPrefix = "memory-delimiters-",
                    firstVisibleAddress = { lazyListState.firstVisibleRowAddress("memory-row-", radix = 16) },
                    modifier = Modifier.align(Alignment.TopStart),
                )

                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    adapter = rememberScrollbarAdapter(lazyListState)
                )
            }
        }
    }
}

private fun buildMemoryRows(
    bytesPerRow: Int,
    annotatedLayoutEnabled: Boolean,
    regions: List<MemoryRegionAnnotation>,
): List<MemoryRow> {
    val memorySize = 0x10000
    val annotationAddresses = if (annotatedLayoutEnabled) buildAnnotationSplitAddresses(regions) else emptyList()

    return ((0 until memorySize step bytesPerRow) + memorySize + annotationAddresses)
        .filter { it in 0..memorySize }
        .distinct()
        .sorted()
        .zipWithNext()
        .map { (start, endExclusive) ->
            MemoryRow(
                address = start.toString(16).padStart(4, '0').uppercase(),
                startAddress = start,
                byteCount = endExclusive - start,
            )
        }
}

private fun buildMemoryLayout(
    rows: List<MemoryRow>,
    regions: List<MemoryRegionAnnotation>,
    annotatedLayoutEnabled: Boolean,
): MemoryLayout {
    val annotationsByAddress = buildAnnotationsByAddress(
        enabled = annotatedLayoutEnabled,
        addresses = rows.map { it.startAddress },
    )
    val delimitersByAddress = buildDelimitersByAddress(
        enabled = annotatedLayoutEnabled,
        rows = rows,
        regions = regions,
        rowStart = { it.startAddress },
        rowEndInclusive = { it.endAddress },
    )

    return MemoryLayout(
        rows = rows,
        annotationsByAddress = annotationsByAddress,
        delimitersByAddress = delimitersByAddress,
        lazyItemIndexesByAddress = buildLazyItemIndexes(
            rows = rows,
            annotationsByAddress = annotationsByAddress,
            delimiterMap = delimitersByAddress,
            rowStart = { it.startAddress },
        ),
    )
}
