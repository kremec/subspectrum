package com.subbyte.subspectrum.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class WatchEntry(val name: String, val address: String, val dec: String, val hex: String)

@Composable
fun WatchPanel() {
    val watches = listOf(
        WatchEntry("temp", "00 00 39", "0", "00 00 00"),
        WatchEntry("x", "00 00 3C", "9", "00 00 09"),
        WatchEntry("y", "00 00 3F", "4", "00 00 04"),
        WatchEntry("sum", "00 00 42", "0", "00 00 00"),
        WatchEntry("diff", "00 00 45", "0", "00 00 00"),
        WatchEntry("prod", "00 00 48", "0", "00 00 00"),
        WatchEntry("quot", "00 00 4B", "0", "00 00 00"),
        WatchEntry("rem", "00 00 4E", "0", "00 00 00")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp)
    ) {
        Text("Watch", fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        HorizontalDivider()

        // Header
        Row(modifier = Modifier.padding(4.dp)) {
            Text("NAME", modifier = Modifier.width(80.dp), fontFamily = FontFamily.Monospace, fontSize = 10.sp)
            Text("ADDRESS", modifier = Modifier.width(80.dp), fontFamily = FontFamily.Monospace, fontSize = 10.sp)
            Text("DEC", modifier = Modifier.width(50.dp), fontFamily = FontFamily.Monospace, fontSize = 10.sp)
            Text("HEX", fontFamily = FontFamily.Monospace, fontSize = 10.sp)
        }
        HorizontalDivider()

        LazyColumn {
            items(watches) { watch ->
                Row(modifier = Modifier.padding(4.dp)) {
                    Text(watch.name, modifier = Modifier.width(80.dp), fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    Text(watch.address, modifier = Modifier.width(80.dp), fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    Text(watch.dec, modifier = Modifier.width(50.dp), fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    Text(watch.hex, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                }
            }
        }
    }
}