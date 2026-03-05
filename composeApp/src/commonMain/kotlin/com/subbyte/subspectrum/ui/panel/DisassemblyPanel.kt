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
import androidx.compose.material.icons.outlined.Circle
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
import com.subbyte.subspectrum.proc.instructions.Instructions
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

data class DisassemblyRow(
    val address: String,
    val bytes: String,
    val operation: String,
    val startAddress: Int
)

@Composable
fun DisassemblyPanel() {
    var uiVersion by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        var dirty = true
        launch {
            merge(
                Memory.memorySet.invalidations,
                Registers.specialPurposeRegisters.pcInvalidations,
            ).conflate().collect { dirty = true }
        }

        while (true) {
            withFrameNanos { }
            if (dirty) {
                dirty = false
                uiVersion++
            }
        }
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
        val pc = Registers.specialPurposeRegisters.getPC()

        LaunchedEffect(uiVersion) {
            val pcRowIndex = pc.toUShort().toInt()

            val visible = lazyListState.layoutInfo.visibleItemsInfo
            val isPcRowVisible =
                visible.any { it.index == pcRowIndex } && visible.indexOfFirst { it.index == pcRowIndex } !in listOf(
                    0,
                    visible.size
                )
            if (isPcRowVisible) return@LaunchedEffect

            lazyListState.scrollToItem(pcRowIndex)
        }

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(state = lazyListState) {
                items(count = 0x10000, key = { it }) { address ->
                    val row = remember(address, uiVersion) {
                        decodeDisassemblyRow(address)
                    }
                    val textColor =
                        if (row.startAddress == pc.toInt()) Color.Red else Color.Black

                    val breakpoints by Processor.breakpoints
                    val breakpointSet = breakpoints.contains(row.startAddress)

                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
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

private fun decodeDisassemblyRow(address: Int): DisassemblyRow {
    return try {
        val decodedInstruction = Instructions.decode(address.toUShort())
        val instruction = decodedInstruction.instruction
        DisassemblyRow(
            address = address.toString(16).padStart(4, '0').uppercase(),
            bytes = instruction.bytes.joinToString(" ") { byte ->
                byte.toInt().and(0xFF).toString(16).padStart(2, '0').uppercase()
            },
            operation = instruction.toString(),
            startAddress = address,
        )
    } catch (_: Exception) {
        val byte = Memory.memorySet.getMemoryCell(address.toUShort())
        DisassemblyRow(
            address = address.toString(16).padStart(4, '0').uppercase(),
            bytes = byte.toInt().and(0xFF).toString(16).padStart(2, '0').uppercase(),
            operation = "BYTE",
            startAddress = address,
        )
    }
}
