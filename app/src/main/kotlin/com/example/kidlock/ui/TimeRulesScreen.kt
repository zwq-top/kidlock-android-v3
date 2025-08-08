
package com.example.kidlock.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TimeRulesScreen(
    startDefault: String,
    endDefault: String,
    onSaveWindow: (String,String) -> Unit
) {
    var start by remember { mutableStateOf(startDefault) }
    var end by remember { mutableStateOf(endDefault) }
    Column(Modifier.fillMaxWidth().padding(12.dp)) {
        Text("允许使用时段（24小时制，如 06:00 - 21:00）", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = start, onValueChange = { start = it }, label = { Text("开始时间") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = end, onValueChange = { end = it }, label = { Text("结束时间") })
        Spacer(Modifier.height(12.dp))
        Button(onClick = { onSaveWindow(start, end) }) { Text("保存时段") }
    }
}
