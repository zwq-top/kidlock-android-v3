
package com.example.kidlock

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kidlock.data.Prefs
import com.example.kidlock.ui.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var selectedTab by remember { mutableStateOf(0) }
                val tabs = listOf("基础设置", "拦截应用", "允许时段", "配额")
                Column(Modifier.fillMaxSize()) {
                    TabRow(selectedTabIndex = selectedTab) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title) }
                            )
                        }
                    }
                    when (selectedTab) {
                        0 -> BasicSettings()
                        1 -> AppList()
                        2 -> TimeRules()
                        3 -> Quotas()
                    }
                }
            }
        }
    }

    @Composable
    private fun BasicSettings() {
        var pin by remember { mutableStateOf(Prefs.getPin(this)) }
        var blocked by remember { mutableStateOf(Prefs.getBlocked(this).joinToString("\n")) }
        Column(Modifier.padding(16.dp)) {
            Text("儿童锁设置", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text("PIN（默认1234）") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = blocked,
                onValueChange = { blocked = it },
                label = { Text("拦截包名（每行一个）") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 5
            )

            Spacer(Modifier.height(12.dp))
            Button(onClick = {
                Prefs.savePin(this@MainActivity, pin)
                Prefs.saveBlocked(
                    this@MainActivity,
                    blocked.lines().map { it.trim() }.filter { it.isNotEmpty() }.toSet()
                )
            }) { Text("保存") }

            Spacer(Modifier.height(12.dp))
            Button(onClick = {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }) { Text("打开无障碍设置（启用儿童锁服务）") }

            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this@MainActivity)) {
                    startActivity(Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    ))
                }
            }) { Text("授予悬浮窗权限（可选）") }
        }
    }

    @Composable
    private fun AppList() {
        val vm: AppListViewModel = viewModel(factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application))
        val apps by vm.apps.collectAsState()
        var blocked by remember { mutableStateOf(Prefs.getBlocked(this)) }

        LaunchedEffect(Unit) { vm.load() }

        AppListScreen(
            allApps = apps,
            blocked = blocked,
            onToggle = { pkg, enable ->
                blocked = if (enable) blocked + pkg else blocked - pkg
                Prefs.saveBlocked(this, blocked)
            }
        )
    }

    @Composable
    private fun TimeRules() {
        val (start, end) = Prefs.getAllowedWindow(this)
        TimeRulesScreen(
            startDefault = start,
            endDefault = end,
            onSaveWindow = { s, e -> Prefs.setAllowedWindow(this, s, e) }
        )
    }

    @Composable
    private fun Quotas() {
        val vm: AppListViewModel = viewModel(factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application))
        val apps by vm.apps.collectAsState()
        val quotasState = remember { mutableStateOf(Prefs.getQuota(this)) }
        LaunchedEffect(Unit) { vm.load() }

        QuotaScreen(
            apps = apps,
            quotas = quotasState.value,
            onSetQuota = { pkg, min ->
                val m = quotasState.value.toMutableMap()
                if (min <= 0) m.remove(pkg) else m[pkg] = min
                quotasState.value = m
                Prefs.saveQuota(this, m)
            }
        )
    }
}
