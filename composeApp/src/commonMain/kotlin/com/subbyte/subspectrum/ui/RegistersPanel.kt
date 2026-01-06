package com.subbyte.subspectrum.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.subbyte.subspectrum.base.Memory
import com.subbyte.subspectrum.base.RegisterSet
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.Word
import kotlinx.coroutines.flow.conflate

@Composable
fun Register(registerName: String, registerValueString: String) {
    Row {
        Text(registerName, fontWeight = FontWeight.Light, modifier = Modifier.width(40.dp))
        Text(registerValueString, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Light)
    }
}

@Composable
fun Register8(registerName: String, registerValue: Byte) {
    Register(registerName, registerValue.toHexString().padStart(2, '0').uppercase())
}

@Composable
fun Register16(registerName: String, registerValue: Word) {
    Register(registerName, registerValue.toHexString().padStart(2, '0').uppercase())
}

@Composable
fun RegisterSetPanel(
    title: String,
    registerNameSuffix: String,
    isActive: Boolean,
    registerSet: RegisterSet,
    modifier: Modifier,
    version: Int
) {
    val v = version
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
            Register8(registerName = "A$registerNameSuffix", registerValue = registerSet.getA())
            Register8(registerName = "F$registerNameSuffix", registerValue = registerSet.getF())
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Register8(registerName = "B$registerNameSuffix", registerValue = registerSet.getB())
            Register8(registerName = "C$registerNameSuffix", registerValue = registerSet.getC())
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Register8(registerName = "D$registerNameSuffix", registerValue = registerSet.getD())
            Register8(registerName = "E$registerNameSuffix", registerValue = registerSet.getE())
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Register8(registerName = "H$registerNameSuffix", registerValue = registerSet.getH())
            Register8(registerName = "L$registerNameSuffix", registerValue = registerSet.getL())
        }
    }
}

@Composable
fun RegistersPanel() {
    var normalRegisterSetVersion by remember { mutableIntStateOf(0) }
    var shadowRegisterSetVersion by remember { mutableIntStateOf(0) }
    var specialPurposeRegistersVersion by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        Registers.normalRegisterSet.invalidations
            .conflate()
            .collect { normalRegisterSetVersion++ }
    }
    LaunchedEffect(Unit) {
        Registers.shadowRegisterSet.invalidations
            .conflate()
            .collect { shadowRegisterSetVersion++ }
    }
    LaunchedEffect(Unit) {
        Registers.specialPurposeRegisters.invalidations
            .conflate()
            .collect { specialPurposeRegistersVersion++ }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        Text("Registers", modifier = Modifier.padding(top = 8.dp))
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

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
                        version = normalRegisterSetVersion
                    )
                    RegisterSetPanel(
                        title = "Alternate register set",
                        registerNameSuffix = "'",
                        isActive = Registers.registerSet === Registers.shadowRegisterSet,
                        registerSet = Registers.shadowRegisterSet,
                        modifier = Modifier.weight(1f),
                        version = shadowRegisterSetVersion
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
                        version = normalRegisterSetVersion
                    )
                    RegisterSetPanel(
                        title = "Alternate register set",
                        registerNameSuffix = "'",
                        isActive = Registers.registerSet === Registers.shadowRegisterSet,
                        registerSet = Registers.shadowRegisterSet,
                        modifier = Modifier.fillMaxWidth(),
                        version = shadowRegisterSetVersion
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
            val v = specialPurposeRegistersVersion
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
                    Register8(registerName = "I", registerValue = Registers.specialPurposeRegisters.getI())
                    Register8(registerName = "R", registerValue = Registers.specialPurposeRegisters.getR())
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Register16("IX", Registers.specialPurposeRegisters.getIX())
                    Register16("IY", Registers.specialPurposeRegisters.getIY())
                    Register16("SP", Registers.specialPurposeRegisters.getSP())
                    Register16("PC", Registers.specialPurposeRegisters.getPC())
                }
            }
        }
    }
}