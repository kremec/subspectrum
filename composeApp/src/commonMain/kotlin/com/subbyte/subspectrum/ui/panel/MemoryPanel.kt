package com.subbyte.subspectrum.ui.panel

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.HorizontalDivider
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
import kotlinx.coroutines.flow.conflate

@Composable
fun MemoryPanel() {
    var version by remember { mutableIntStateOf(0) }
    var pcVersion by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        Memory.memorySet.invalidations
            .conflate() // collapses bursts of changes into fewer recomposes
            .collect { version++ }
    }
    LaunchedEffect(Unit) {
        Registers.specialPurposeRegisters.pcInvalidations
            .conflate()
            .collect { pcVersion++ }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp)
    ) {
        Text(
            "Memory",
            modifier = Modifier.padding(bottom = 8.dp)
        )
        HorizontalDivider()

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val bytesPerRow = when {
                maxWidth < 300.dp -> 4
                maxWidth < 550.dp -> 8
                else -> 16
            }

            val memoryRows = remember(bytesPerRow, version) {
                buildMemoryRows(bytesPerRow)
            }
            val lazyListState = rememberLazyListState()
            val pc = Registers.specialPurposeRegisters.getPC()
            val prevBytesPerRow = remember { mutableStateOf(bytesPerRow) }

            LaunchedEffect(pcVersion) {
                val pcRowIndex = memoryRows.indexOfLast { row -> row.startAddress < pc.toUShort().toInt() }
                if (pcRowIndex == -1) return@LaunchedEffect

                val visible = lazyListState.layoutInfo.visibleItemsInfo
                val isPcRowVisible = visible.any { it.index == pcRowIndex } && visible.indexOfFirst { it.index == pcRowIndex } !in listOf(0, visible.size)
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
                    items(memoryRows, key = { it.address }) { row ->
                        Row(modifier = Modifier.padding(4.dp)) {
                            Text(
                                row.address,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Light,
                                modifier = Modifier.width(60.dp)
                            )
                            row.bytes.forEachIndexed { index, byteValue ->
                                val byteAddress = (row.startAddress + index).toUShort()
                                val textColor =
                                    if (byteAddress == pc.toUShort()) Color.Red else Color.Black
                                Text(
                                    byteValue,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Light,
                                    color = textColor,
                                    modifier = Modifier.width(30.dp)
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
        val bytes = mutableListOf<String>()
        for (i in 0 until bytesPerRow) {
            val byte = Memory.memorySet.getMemoryCell((address + i).toUShort())
            bytes.add(byte.toUByte().toString(16).padStart(2, '0').uppercase())
        }

        rows.add(
            MemoryRow(
                address = address.toString(16).padStart(4, '0').uppercase(),
                startAddress = address,
                bytes = bytes
            )
        )

        address += bytesPerRow
    }

    return rows
}

private data class MemoryRow(
    val address: String,
    val startAddress: Int,
    val bytes: List<String>
)