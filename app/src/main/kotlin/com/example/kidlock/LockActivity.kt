
package com.example.kidlock

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kidlock.data.Prefs

class LockActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFinishOnTouchOutside(false)

        setContent {
            MaterialTheme {
                var input by remember { mutableStateOf("") }
                val pin = Prefs.getPin(this)

                Column(Modifier.padding(24.dp)) {
                    Text("家长控制", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(12.dp))
                    Text("此应用已被锁定，输入PIN继续。")
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        label = { Text("PIN") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = {
                        if (input == pin) {
                            finish()
                        }
                    }) {
                        Text("解锁")
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        // Disable back to prevent bypass
    }
}
