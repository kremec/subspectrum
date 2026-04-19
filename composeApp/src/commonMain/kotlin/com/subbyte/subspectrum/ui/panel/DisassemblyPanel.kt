package com.subbyte.subspectrum.ui.panel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.LocationSearching
import androidx.compose.material.icons.outlined.Circle
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
import com.subbyte.subspectrum.base.annotations.MemoryInstructionAnnotation
import com.subbyte.subspectrum.base.annotations.MemoryRegionAnnotation
import com.subbyte.subspectrum.base.annotations.RAMSectionRegistry
import com.subbyte.subspectrum.base.annotations.ROMInstructionRegistry
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.proc.instructions.Instructions
import com.subbyte.subspectrum.ui.components.AnnotationColumn
import com.subbyte.subspectrum.ui.components.AnnotationTooltip
import com.subbyte.subspectrum.ui.components.IconButton
import com.subbyte.subspectrum.ui.components.HexValueEditor
import com.subbyte.subspectrum.ui.components.RegionDelimiterRow
import com.subbyte.subspectrum.ui.components.StickyAnnotationOverlay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class DisassemblyRow(
    val address: String,
    val bytes: ByteArray,
    val operation: String,
    val startAddress: Address
) {
    fun containsAddress(address: Address): Boolean {
        val start = startAddress.toUInt()
        val endExclusive = start + bytes.size.toUInt()
        val value = address.toUInt()
        return value in start until endExclusive
    }
}

private data class DisassemblyLayout(
    val rows: List<DisassemblyRow>,
    val annotationsByAddress: Map<Int, List<MemoryAnnotation>>,
    val delimitersByAddress: Map<Int, List<MemoryRegionAnnotation>>,
    val instructionAnnotationsByAddress: Map<Address, MemoryInstructionAnnotation>,
    val lazyItemIndexesByAddress: Map<Int, Int>,
)

@Suppress("DEPRECATION")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DisassemblyPanel() {
    var renderVersion by remember { mutableIntStateOf(0) }
    var rowsVersion by remember { mutableIntStateOf(0) }
    var trackPc by remember { mutableStateOf(false) }
    var annotatedLayoutEnabled by remember { mutableStateOf(false) }
    val editingEnabled = !Processor.running.value

    LaunchedEffect(Unit) {
        var renderDirty = true
        var rowsDirty = true

        launch {
            Memory.memorySet.invalidations.conflate().collect {
                renderDirty = true
                rowsDirty = true
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
            if (rowsDirty) {
                rowsDirty = false
                rowsVersion++
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
            Text("Disassembly")
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

        val breakpoints by Processor.breakpoints
        val regions = remember(renderVersion) {
            RAMSectionRegistry.allRegions(Memory.memorySet)
        }
        var rows by remember { mutableStateOf<List<DisassemblyRow>>(emptyList()) }
        LaunchedEffect(rowsVersion) {
            rows = withContext(Dispatchers.Default) { decodeDisassemblyRows() }
        }
        val layout = remember(rows, regions, annotatedLayoutEnabled) {
            buildDisassemblyLayout(
                rows = rows,
                regions = regions,
                annotatedLayoutEnabled = annotatedLayoutEnabled,
            )
        }

        val lazyListState = rememberLazyListState()
        val pcAddress = Registers.specialPurposeRegisters.getPC().toUShort()

        LaunchedEffect(renderVersion, trackPc) {
            if (!trackPc) return@LaunchedEffect

            val pcRowIndex = layout.rows.indexOfFirst { it.containsAddress(pcAddress) }
            if (pcRowIndex == -1) return@LaunchedEffect

            lazyListState.scrollToRowIfNeeded(
                rowIndex = pcRowIndex,
                rowAddress = layout.rows[pcRowIndex].startAddress.toInt(),
                lazyItemIndexesByAddress = layout.lazyItemIndexesByAddress,
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(state = lazyListState) {
                layout.rows.forEach { row ->
                    val rowAnnotations = layout.annotationsByAddress[row.startAddress.toInt()].orEmpty()
                    val rowDelimiters = layout.delimitersByAddress[row.startAddress.toInt()].orEmpty()
                    val instructionAnnotation = layout.instructionAnnotationsByAddress[row.startAddress]
                    val textColor =
                        if (row.containsAddress(pcAddress)) Color.Red else Color.Black

                    val breakpointSet = breakpoints.contains(row.startAddress)

                    if (annotatedLayoutEnabled && rowDelimiters.isNotEmpty()) {
                        stickyHeader(key = "disassembly-delimiters-${row.startAddress}") {
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
                        item(key = "disassembly-annotation-${row.startAddress}") {
                            AnnotationColumn(
                                annotations = rowAnnotations,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .padding(horizontal = 4.dp, vertical = 4.dp),
                            )
                        }
                    }

                    item(key = "disassembly-row-${row.startAddress}") {
                        Row(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .align(Alignment.CenterVertically)
                                    .clickable(
                                        indication = null,
                                        interactionSource = null
                                    ) {
                                        Processor.breakpoints.value = if (breakpointSet)
                                            breakpoints - row.startAddress
                                        else
                                            breakpoints + row.startAddress
                                    }
                            ) {
                                Icon(
                                    imageVector = if (breakpointSet) Icons.Filled.Circle else Icons.Outlined.Circle,
                                    contentDescription = if (breakpointSet) "Remove breakpoint" else "Add breakpoint",
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            Text(
                                row.address,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Light,
                                color = textColor,
                                modifier = Modifier.width(90.dp)
                            )
                            Row(
                                modifier = Modifier.width(130.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                row.bytes.forEachIndexed { byteIndex, byteValue ->
                                    val byteAddress =
                                        (row.startAddress.toUInt() + byteIndex.toUInt()).toUShort()
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
                                            Memory.memorySet.setMemoryCell(byteAddress, it.toByte())
                                        },
                                    )
                                }
                            }
                            if (instructionAnnotation == null) {
                                DisassemblyOperationText(
                                    operation = row.operation,
                                    color = textColor,
                                    modifier = Modifier.weight(1f),
                                )
                            } else {
                                AnnotationTooltip(
                                    tooltip = instructionAnnotation.description,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    DisassemblyOperationText(
                                        operation = row.operation,
                                        color = textColor,
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
                annotationKeyPrefix = "disassembly-annotation-",
                delimiterKeyPrefix = "disassembly-delimiters-",
                firstVisibleAddress = { lazyListState.firstVisibleRowAddress("disassembly-row-") },
                modifier = Modifier.align(Alignment.TopStart),
            )

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd),
                adapter = rememberScrollbarAdapter(lazyListState)
            )
        }
    }
}

@Composable
private fun DisassemblyOperationText(
    operation: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        operation,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Light,
        color = color,
        modifier = modifier,
    )
}

private fun buildDisassemblyLayout(
    rows: List<DisassemblyRow>,
    regions: List<MemoryRegionAnnotation>,
    annotatedLayoutEnabled: Boolean,
): DisassemblyLayout {
    val annotationsByAddress = buildAnnotationsByAddress(
        enabled = annotatedLayoutEnabled,
        addresses = rows.map { it.startAddress.toInt() },
    )
    val delimitersByAddress = buildDelimitersByAddress(
        enabled = annotatedLayoutEnabled,
        rows = rows,
        regions = regions,
        rowStart = { it.startAddress.toInt() },
        rowEndInclusive = { it.startAddress.toInt() + it.bytes.size - 1 },
    )

    return DisassemblyLayout(
        rows = rows,
        annotationsByAddress = annotationsByAddress,
        delimitersByAddress = delimitersByAddress,
        instructionAnnotationsByAddress = if (annotatedLayoutEnabled) {
            ROMInstructionRegistry.instructionsByAddress
        } else {
            emptyMap()
        },
        lazyItemIndexesByAddress = buildLazyItemIndexes(
            rows = rows,
            annotationsByAddress = annotationsByAddress,
            delimiterMap = delimitersByAddress,
            rowStart = { it.startAddress.toInt() },
        ),
    )
}

private fun decodeDisassemblyRows(): List<DisassemblyRow> {
    return buildList {
        var address = 0
        while (address <= 0xFFFF) {
            val row = decodeDisassemblyRow(address.toUShort())
            add(row)
            address += row.bytes.size.coerceAtLeast(1)
        }
    }
}

private fun decodeDisassemblyRow(address: Address): DisassemblyRow {
    return try {
        val decodedInstruction = Instructions.decode(address)
        val instruction = decodedInstruction.instruction
        DisassemblyRow(
            address = address.toString(16).padStart(4, '0').uppercase(),
            bytes = ByteArray(instruction.bytes.size) { instruction.bytes[it] },
            operation = instruction.toString(),
            startAddress = address,
        )
    } catch (_: Exception) {
        val byte = Memory.memorySet.getMemoryCell(address)
        DisassemblyRow(
            address = address.toString(16).padStart(4, '0').uppercase(),
            bytes = byteArrayOf(byte),
            operation = "BYTE",
            startAddress = address,
        )
    }
}
