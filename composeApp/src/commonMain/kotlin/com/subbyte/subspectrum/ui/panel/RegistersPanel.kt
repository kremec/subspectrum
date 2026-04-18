package com.subbyte.subspectrum.ui.panel

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.subbyte.subspectrum.base.RegisterSet
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.proc.Processor
import com.subbyte.subspectrum.ui.components.HexValueEditor
import com.subbyte.subspectrum.units.Word
import com.subbyte.subspectrum.units.getBit
import com.subbyte.subspectrum.units.setBit
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

@Composable
fun Register(registerName: String, valueEditor: @Composable () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(registerName, fontWeight = FontWeight.Light, modifier = Modifier.width(40.dp))
        valueEditor()
    }
}

@Composable
fun Register8(
    registerName: String,
    registerValue: Byte,
    enabled: Boolean,
    onValueCommitted: (Byte) -> Unit,
) {
    Register(registerName) {
        HexValueEditor(
            value = registerValue.toHexString().padStart(2, '0').uppercase(),
            digits = 2,
            enabled = enabled,
            onValueCommitted = { onValueCommitted(it.toByte()) },
        )
    }
}

@Composable
fun Register16(
    registerName: String,
    registerValue: Word,
    enabled: Boolean,
    onValueCommitted: (Word) -> Unit,
) {
    Register(registerName) {
        HexValueEditor(
            value = registerValue.toUShort().toString(16).padStart(4, '0').uppercase(),
            digits = 4,
            enabled = enabled,
            onValueCommitted = { onValueCommitted(it.toUShort().toShort()) },
        )
    }
}

@Composable
fun FlagState(
    flagName: String,
    isSet: Boolean,
    enabled: Boolean,
    onValueCommitted: (Boolean) -> Unit,
) {
    val textColor = if (enabled) Color.Black else Color.Black.copy(alpha = 0.5f)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(flagName, fontWeight = FontWeight.Light, color = textColor)
        HexValueEditor(
            value = if (isSet) "1" else "0",
            digits = 1,
            enabled = enabled,
            allowedCharacters = "01",
            onValueCommitted = { onValueCommitted(it != 0) }
        )
    }
}

@Composable
fun RegisterFlags(registerSet: RegisterSet, editingEnabled: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val f = registerSet.getF()
        FlagState("S", registerSet.getSFlag(), editingEnabled) {
            registerSet.setSFlag(it)
        }
        FlagState("Z", registerSet.getZFlag(), editingEnabled) {
            registerSet.setZFlag(it)
        }
        FlagState("F5", f.getBit(5), editingEnabled) {
            registerSet.setF(registerSet.getF().setBit(5, it))
        }
        FlagState("H", registerSet.getHFlag(), editingEnabled) {
            registerSet.setHFlag(it)
        }
        FlagState("F3", f.getBit(3), editingEnabled) {
            registerSet.setF(registerSet.getF().setBit(3, it))
        }
        FlagState("PV", registerSet.getPVFlag(), editingEnabled) {
            registerSet.setPVFlag(it)
        }
        FlagState("N", registerSet.getNFlag(), editingEnabled) {
            registerSet.setNFlag(it)
        }
        FlagState("C", registerSet.getCFlag(), editingEnabled) {
            registerSet.setCFlag(it)
        }
    }
}

@Composable
fun RegisterSetPanel(
    title: String,
    registerNameSuffix: String,
    isActive: Boolean,
    registerSet: RegisterSet,
    modifier: Modifier,
    editingEnabled: Boolean,
) {
    Column(
        modifier = modifier
            .border(
                width = if (isActive) 2.dp else 1.dp,
                color = if (isActive) Color.Black else Color.LightGray
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            title,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Register8(
                registerName = "A$registerNameSuffix",
                registerValue = registerSet.getA(),
                enabled = editingEnabled,
                onValueCommitted = { value -> registerSet.setA(value) }
            )
            Register8(
                registerName = "F$registerNameSuffix",
                registerValue = registerSet.getF(),
                enabled = editingEnabled,
                onValueCommitted = { value -> registerSet.setF(value) }
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Register8(
                registerName = "B$registerNameSuffix",
                registerValue = registerSet.getB(),
                enabled = editingEnabled,
                onValueCommitted = { value -> registerSet.setB(value) }
            )
            Register8(
                registerName = "C$registerNameSuffix",
                registerValue = registerSet.getC(),
                enabled = editingEnabled,
                onValueCommitted = { value -> registerSet.setC(value) }
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Register8(
                registerName = "D$registerNameSuffix",
                registerValue = registerSet.getD(),
                enabled = editingEnabled,
                onValueCommitted = { value -> registerSet.setD(value) }
            )
            Register8(
                registerName = "E$registerNameSuffix",
                registerValue = registerSet.getE(),
                enabled = editingEnabled,
                onValueCommitted = { value -> registerSet.setE(value) }
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Register8(
                registerName = "H$registerNameSuffix",
                registerValue = registerSet.getH(),
                enabled = editingEnabled,
                onValueCommitted = { value -> registerSet.setH(value) }
            )
            Register8(
                registerName = "L$registerNameSuffix",
                registerValue = registerSet.getL(),
                enabled = editingEnabled,
                onValueCommitted = { value -> registerSet.setL(value) }
            )
        }
        RegisterFlags(registerSet = registerSet, editingEnabled = editingEnabled)
    }
}

@Composable
fun RegistersPanel() {
    var uiVersion by remember { mutableIntStateOf(0) }
    val editingEnabled = !Processor.running.value
    LaunchedEffect(Unit) {
        var dirty = true
        launch {
            merge(
                Registers.normalRegisterSet.invalidations,
                Registers.shadowRegisterSet.invalidations,
                Registers.specialPurposeRegisters.invalidations,
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
            .padding(horizontal = 8.dp)
    ) {
        Text("Registers", modifier = Modifier.padding(top = 8.dp))
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        key(uiVersion) {
            // Main/alternate register sets - responsive layout
            BoxWithConstraints(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                if (maxWidth > 400.dp) {
                    // Wide layout - side by side
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RegisterSetPanel(
                            title = "Main register set",
                            registerNameSuffix = "",
                            isActive = Registers.registerSet === Registers.normalRegisterSet,
                            registerSet = Registers.normalRegisterSet,
                            modifier = Modifier.weight(1f),
                            editingEnabled = editingEnabled
                        )
                        RegisterSetPanel(
                            title = "Alternate register set",
                            registerNameSuffix = "'",
                            isActive = Registers.registerSet === Registers.shadowRegisterSet,
                            registerSet = Registers.shadowRegisterSet,
                            modifier = Modifier.weight(1f),
                            editingEnabled = editingEnabled
                        )
                    }
                } else {
                    // Narrow layout - stacked vertically
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RegisterSetPanel(
                            title = "Main register set",
                            registerNameSuffix = "",
                            isActive = Registers.registerSet === Registers.normalRegisterSet,
                            registerSet = Registers.normalRegisterSet,
                            modifier = Modifier.fillMaxWidth(),
                            editingEnabled = editingEnabled
                        )
                        RegisterSetPanel(
                            title = "Alternate register set",
                            registerNameSuffix = "'",
                            isActive = Registers.registerSet === Registers.shadowRegisterSet,
                            registerSet = Registers.shadowRegisterSet,
                            modifier = Modifier.fillMaxWidth(),
                            editingEnabled = editingEnabled
                        )
                    }
                }
            }

            // Special purpose registers - two columns
            Column(
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = Color.Black
                    )
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Special purpose registers",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        Register8(
                            registerName = "I",
                            registerValue = Registers.specialPurposeRegisters.getI(),
                            enabled = editingEnabled,
                            onValueCommitted = { value -> Registers.specialPurposeRegisters.setI(value) }
                        )
                        Register8(
                            registerName = "R",
                            registerValue = Registers.specialPurposeRegisters.getR(),
                            enabled = editingEnabled,
                            onValueCommitted = { value -> Registers.specialPurposeRegisters.setR(value) }
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Register16(
                            registerName = "IX",
                            registerValue = Registers.specialPurposeRegisters.getIX(),
                            enabled = editingEnabled,
                            onValueCommitted = { value -> Registers.specialPurposeRegisters.setIX(value) }
                        )
                        Register16(
                            registerName = "IY",
                            registerValue = Registers.specialPurposeRegisters.getIY(),
                            enabled = editingEnabled,
                            onValueCommitted = { value -> Registers.specialPurposeRegisters.setIY(value) }
                        )
                        Register16(
                            registerName = "SP",
                            registerValue = Registers.specialPurposeRegisters.getSP(),
                            enabled = editingEnabled,
                            onValueCommitted = { value -> Registers.specialPurposeRegisters.setSP(value) }
                        )
                        Register16(
                            registerName = "PC",
                            registerValue = Registers.specialPurposeRegisters.getPC(),
                            enabled = editingEnabled,
                            onValueCommitted = { value -> Registers.specialPurposeRegisters.setPC(value) }
                        )
                    }
                }
            }
        }
    }
}
