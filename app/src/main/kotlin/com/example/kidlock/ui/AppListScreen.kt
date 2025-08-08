
package com.example.kidlock.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppListScreen(
    allApps: List<AppInfo>,
    blocked: Set<String>,
    onToggle: (String, Boolean) -> Unit
) {
    var query by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("搜索应用") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        LazyColumn(Modifier.fillMaxSize()) {
            val filtered = allApps.filter {
                it.label.contains(query, ignoreCase = true) || it.packageName.contains(query, ignoreCase = true)
            }
            items(filtered) { app ->
                val checked = blocked.contains(app.packageName)
                Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Checkbox(checked = checked, onCheckedChange = { onToggle(app.packageName, it) })
                    Column(Modifier.padding(start = 8.dp)) {
                        Text(app.label, style = MaterialTheme.typography.bodyLarge)
                        Text(app.packageName, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Divider()
            }
        }
    }
}
