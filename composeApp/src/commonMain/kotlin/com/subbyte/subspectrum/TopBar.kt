package com.subbyte.subspectrum

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.subbyte.subspectrum.base.Memory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TopBar() {
    val scope = rememberCoroutineScope()

    MaterialTheme {
        Column(modifier = Modifier.background(Color.White)) {
            Spacer(modifier = Modifier.height(25.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        scope.launch(Dispatchers.Default) {
                            Memory.memorySet.setMemoryCells(
                                startAddress = 0x0000.toUShort(),
                                data = byteArrayOf(0x06, 0xAB.toByte())
                            )
                            Memory.memorySet.setMemoryCells(
                                startAddress = 0x0002.toUShort(),
                                data = byteArrayOf(0xFD.toByte(), 0x36, 0x12, 0xAB.toByte())
                            )
//                            var i = 0x00
//                            while(true) {
//                                Memory.memorySet.setMemoryCell(
//                                    address = 0x0000.toUShort(),
//                                    value = i.toByte()
//                                )
//                                Registers.registerSet.setA(i.toByte())
//                                Registers.specialPurposeRegisters.setIX(i.toShort())
//                                i++
//                            }
                        }
                    }
                ) {
                    Text("Load")
                }
                Button(
                    onClick = {},
                ) {
                    Text("Reset")
                }
                Button(
                    onClick = {},
                ) {
                    Text("Step")
                }
                Button(
                    onClick = {},
                ) {
                    Text("Run")
                }
            }
        }
    }
}