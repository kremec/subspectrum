package com.subbyte.subspectrum.ui.panel

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationSearching
import androidx.compose.material.icons.outlined.MyLocation
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
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.ui.components.IconButton
import com.subbyte.subspectrum.ui.components.HexValueEditor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MemoryPanel() {
    var renderVersion by remember { mutableIntStateOf(0) }
    var trackPc by remember { mutableStateOf(false) }
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
                onClick = { trackPc = !trackPc },
                tooltip = if (trackPc) "Disable PC tracking" else "Enable PC tracking"
            ) {
                Icon(
                    imageVector = if (trackPc) Icons.Outlined.MyLocation else Icons.Outlined.LocationSearching,
                    contentDescription = if (trackPc) "PC tracking enabled" else "PC tracking disabled"
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

            var rows by remember { mutableStateOf<List<MemoryRow>>(emptyList()) }
            LaunchedEffect(bytesPerRow) {
                rows = withContext(Dispatchers.Default) {
                    buildMemoryRows(bytesPerRow)
                }
            }

            val lazyListState = rememberLazyListState()
            val pc = Registers.specialPurposeRegisters.getPC()
            val prevBytesPerRow = remember { mutableStateOf(bytesPerRow) }

            LaunchedEffect(renderVersion, trackPc) {
                if (!trackPc) return@LaunchedEffect
                if (rows.isEmpty()) return@LaunchedEffect

                val pcRowIndex = pc.toUShort().toInt() / bytesPerRow

                val visible = lazyListState.layoutInfo.visibleItemsInfo
                val pcVisibleIndex = visible.indexOfFirst { it.index == pcRowIndex }
                val isPcRowVisible = pcVisibleIndex != -1 && pcVisibleIndex !in listOf(0, visible.lastIndex)
                if (isPcRowVisible) return@LaunchedEffect

                lazyListState.scrollToItem(pcRowIndex)
            }

            // When bytesPerRow changes, scroll to maintain the same address
            LaunchedEffect(bytesPerRow) {
                if (bytesPerRow != prevBytesPerRow.value) {
                    val currentAddress =
                        lazyListState.firstVisibleItemIndex * prevBytesPerRow.value
                    val newRowIndex = currentAddress / bytesPerRow
                    lazyListState.scrollToItem(newRowIndex)
                    prevBytesPerRow.value = bytesPerRow
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(state = lazyListState) {
                    items(rows, key = { it.address }) { row ->
                        val rowBytes = remember(row.startAddress, bytesPerRow, renderVersion) {
                            List(bytesPerRow) { index ->
                                val byteAddress = (row.startAddress + index).toUShort()
                                byteAddress to Memory.memorySet.getMemoryCell(byteAddress)
                            }
                        }

                        Row(
                            modifier = Modifier.padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                row.address,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Light,
                                modifier = Modifier.width(60.dp)
                            )
                            rowBytes.forEach { (byteAddress, byteValue) ->
                                val textColor =
                                    if (byteAddress == pc.toUShort()) Color.Red else Color.Black
                                HexValueEditor(
                                    value = byteValue
                                        .toUByte()
                                        .toString(16)
                                        .padStart(2, '0')
                                        .uppercase(),
                                    digits = 2,
                                    color = textColor,
                                    enabled = editingEnabled,
                                    onValueCommitted = { Memory.memorySet.setMemoryCell(byteAddress, it.toByte()) },
                                )
                            }
                        }
                    }
                }

                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    adapter = rememberScrollbarAdapter(lazyListState)
                )
            }
        }
    }
}

private fun buildMemoryRows(bytesPerRow: Int): List<MemoryRow> {
    val rows = mutableListOf<MemoryRow>()
    var address = 0

    while (address < 0x10000) {
        rows.add(
            MemoryRow(
                address = address.toString(16).padStart(4, '0').uppercase(),
                startAddress = address
            )
        )

        address += bytesPerRow
    }

    return rows
}

private data class MemoryRow(
    val address: String,
    val startAddress: Int
)
