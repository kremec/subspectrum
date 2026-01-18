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
import com.subbyte.subspectrum.proc.instructions.Instructions
import kotlinx.coroutines.flow.conflate

data class DisassemblyRow(
    val address: String,
    val bytes: String,
    val operation: String,
    val startAddress: Int
)

@Composable
fun DisassemblyPanel() {
    var version by remember { mutableIntStateOf(0) }
    var pcVersion by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        Memory.memorySet.invalidations
            .conflate()
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
            "Disassembly",
            modifier = Modifier.padding(bottom = 8.dp)
        )
        HorizontalDivider()

        // Header
        Row(modifier = Modifier.padding(4.dp)) {
            Text(
                "ADDRESS",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Light,
                modifier = Modifier.width(90.dp)
            )
            Text(
                "BYTES",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Light,
                modifier = Modifier.width(130.dp)
            )
            Text(
                "OPERATION",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Light,
                modifier = Modifier.weight(1f)
            )
        }
        HorizontalDivider()

        val disassemblyRows = remember(version) {
            buildDisassemblyRows()
        }
        val lazyListState = rememberLazyListState()
        val pc = Registers.specialPurposeRegisters.getPC()

        LaunchedEffect(pcVersion) {
            val pcRowIndex = disassemblyRows.indexOfFirst { row -> row.startAddress == pc.toUShort().toInt() }
            if (pcRowIndex == -1) return@LaunchedEffect

            val visible = lazyListState.layoutInfo.visibleItemsInfo
            val isPcRowVisible = visible.any { it.index == pcRowIndex } && visible.indexOfFirst { it.index == pcRowIndex } !in listOf(0, visible.size)
            if (isPcRowVisible) return@LaunchedEffect

            lazyListState.scrollToItem(pcRowIndex)
        }

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(state = lazyListState) {
                items(
                    disassemblyRows,
                    key = { it.startAddress }
                ) { row ->
                    val textColor =
                        if (row.startAddress == pc.toInt()) Color.Red else Color.Black

                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            row.address,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Light,
                            color = textColor,
                            modifier = Modifier.width(90.dp)
                        )
                        Text(
                            row.bytes,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Light,
                            color = textColor,
                            modifier = Modifier.width(130.dp)
                        )
                        Text(
                            row.operation,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Light,
                            color = textColor,
                            modifier = Modifier.weight(1f)
                        )
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

private fun buildDisassemblyRows(): List<DisassemblyRow> {
    val rows = mutableListOf<DisassemblyRow>()
    var address = 0

    while (address < 0x10000) {
        try {
            val instruction = Instructions.decode(address.toUShort())
            val addressStr =
                address.toString(16).padStart(4, '0').uppercase()
            val bytesStr = instruction.bytes.joinToString(" ") { byte ->
                byte.toInt().and(0xFF).toString(16).padStart(2, '0')
                    .uppercase()
            }

            val operation = instruction.toString()

            rows.add(
                DisassemblyRow(
                    address = addressStr,
                    bytes = bytesStr,
                    operation = operation,
                    startAddress = address
                )
            )

            address += instruction.bytes.size
        } catch (_: Exception) {
            // Handle unknown opcodes by showing as data
            val byte =
                Memory.memorySet.getMemoryCell(address.toUShort())
            rows.add(
                DisassemblyRow(
                    address = address.toString(16).padStart(4, '0')
                        .uppercase(),
                    bytes =
                        byte.toInt().and(0xFF).toString(16).padStart(2, '0')
                            .uppercase(),
                    operation = "BYTE",
                    startAddress = address
                )
            )
            address++
        }
    }

    return rows
}