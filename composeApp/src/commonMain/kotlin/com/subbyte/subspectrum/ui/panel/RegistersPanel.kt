package com.subbyte.subspectrum.ui.panel

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.subbyte.subspectrum.base.RegisterSet
import com.subbyte.subspectrum.base.Registers
import com.subbyte.subspectrum.units.Word
import com.subbyte.subspectrum.units.getBit
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

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
fun FlagState(flagName: String, isSet: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(flagName, fontWeight = FontWeight.Light)
        Text(
            if (isSet) "1" else "0",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
fun RegisterFlags(registerSet: RegisterSet) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val f = registerSet.getF()
        FlagState("S", registerSet.getSFlag())
        FlagState("Z", registerSet.getZFlag())
        FlagState("F5", f.getBit(5))
        FlagState("H", registerSet.getHFlag())
        FlagState("F3", f.getBit(3))
        FlagState("PV", registerSet.getPVFlag())
        FlagState("N", registerSet.getNFlag())
        FlagState("C", registerSet.getCFlag())
    }
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
        RegisterFlags(registerSet = registerSet)
    }
}

@Composable
fun RegistersPanel() {
    var uiVersion by remember { mutableIntStateOf(0) }
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
                        version = uiVersion
                    )
                    RegisterSetPanel(
                        title = "Alternate register set",
                        registerNameSuffix = "'",
                        isActive = Registers.registerSet === Registers.shadowRegisterSet,
                        registerSet = Registers.shadowRegisterSet,
                        modifier = Modifier.weight(1f),
                        version = uiVersion
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
                        version = uiVersion
                    )
                    RegisterSetPanel(
                        title = "Alternate register set",
                        registerNameSuffix = "'",
                        isActive = Registers.registerSet === Registers.shadowRegisterSet,
                        registerSet = Registers.shadowRegisterSet,
                        modifier = Modifier.fillMaxWidth(),
                        version = uiVersion
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
            val v = uiVersion
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
