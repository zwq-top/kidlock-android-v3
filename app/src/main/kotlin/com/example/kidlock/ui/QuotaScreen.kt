
package com.example.kidlock.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuotaScreen(
    apps: List<AppInfo>,
    quotas: Map<String, Int>,
    onSetQuota: (String, Int) -> Unit
) {
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("每日使用配额（分钟）", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        LazyColumn(Modifier.fillMaxSize()) {
            items(apps) { app ->
                val current = quotas[app.packageName] ?: 0
                var text by remember { mutableStateOf(if (current == 0) "" else current.toString()) }
                Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text(app.label, style = MaterialTheme.typography.bodyLarge)
                        Text(app.packageName, style = MaterialTheme.typography.bodySmall)
                    }
                    OutlinedTextField(
                        value = text,
                        onValueChange = { v -> text = v.filter { it.isDigit() } },
                        label = { Text("分钟") },
                        modifier = Modifier.width(120.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { onSetQuota(app.packageName, text.toIntOrNull() ?: 0) }) { Text("保存") }
                }
                Divider()
            }
        }
    }
}
