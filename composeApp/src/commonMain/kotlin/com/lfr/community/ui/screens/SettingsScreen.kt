package com.lfr.community.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lfr.community.data.repository.CommunityRepository

@Composable
fun SettingsScreen(repository: CommunityRepository) {
    var apiUrl by remember { mutableStateOf("http://127.0.0.1:3001/api") }
    var saved by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("设置", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = apiUrl,
            onValueChange = { apiUrl = it; saved = false },
            label = { Text("API 地址") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Button(
            onClick = {
                repository.updateApiBase(apiUrl.trim())
                saved = true
            }
        ) {
            Text(if (saved) "已保存 ✓" else "保存")
        }

        HorizontalDivider()

        Text("关于", style = MaterialTheme.typography.titleMedium)
        Text("LFR Community v1.0.0", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Compose Multiplatform", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
