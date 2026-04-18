package com.subbyte.subspectrum.ui.panel

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
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.proc.instructions.Instructions
import com.subbyte.subspectrum.ui.components.IconButton
import com.subbyte.subspectrum.ui.components.HexValueEditor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class DisassemblyRow(
    val address: String,
    val bytes: ByteArray,
    val operation: String,
    val startAddress: Address
)

private fun DisassemblyRow.containsAddress(address: Address): Boolean {
    val start = startAddress.toUInt()
    val endExclusive = start + bytes.size.toUInt()
    val value = address.toUInt()
    return value in start until endExclusive
}

@Composable
fun DisassemblyPanel() {
    var renderVersion by remember { mutableIntStateOf(0) }
    var rowsVersion by remember { mutableIntStateOf(0) }
    var trackPc by remember { mutableStateOf(false) }
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

    var rows by remember { mutableStateOf<List<DisassemblyRow>>(emptyList()) }
    LaunchedEffect(rowsVersion) {
        rows = withContext(Dispatchers.Default) { decodeDisassemblyRows() }
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

        // Header
        Row(modifier = Modifier.padding(4.dp)) {
            Spacer(Modifier.width(20.dp))
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

        val lazyListState = rememberLazyListState()
        val pcAddress = Registers.specialPurposeRegisters.getPC().toUShort()
        val breakpoints by Processor.breakpoints

        LaunchedEffect(renderVersion, trackPc) {
            if (!trackPc) return@LaunchedEffect

            val pcRowIndex = rows.indexOfFirst { it.containsAddress(pcAddress) }
            if (pcRowIndex == -1) return@LaunchedEffect

            val visible = lazyListState.layoutInfo.visibleItemsInfo
            val pcVisibleIndex = visible.indexOfFirst { it.index == pcRowIndex }
            val isPcRowVisible = pcVisibleIndex != -1 && pcVisibleIndex !in listOf(0, visible.lastIndex)
            if (isPcRowVisible) return@LaunchedEffect

            lazyListState.scrollToItem(pcRowIndex)
        }

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(state = lazyListState) {
                items(count = rows.size, key = { rows[it].startAddress }) { index ->
                    val row = rows[index]
                    val textColor =
                        if (row.containsAddress(pcAddress)) Color.Red else Color.Black

                    val breakpointSet = breakpoints.contains(row.startAddress)

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

private fun decodeDisassemblyRows(): List<DisassemblyRow> {
    val rows = ArrayList<DisassemblyRow>(0x10000)

    var address = 0
    while (address <= 0xFFFF) {
        val row = decodeDisassemblyRow(address.toUShort())
        rows += row
        address += row.bytes.size.coerceAtLeast(1)
    }

    return rows
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
