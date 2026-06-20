package com.lfr.community.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lfr.community.data.repository.CommunityRepository
import com.lfr.community.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(repository: CommunityRepository, navController: NavController) {
    var apiUrl by remember { mutableStateOf("https://liufarui.top/community-api") }
    var saved by remember { mutableStateOf(false) }
    var loggingOut by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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

        OutlinedButton(
            onClick = {
                scope.launch {
                    loggingOut = true
                    repository.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            },
            enabled = !loggingOut,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loggingOut) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
            }
            Text("退出登录")
        }

        HorizontalDivider()

        Text("关于", style = MaterialTheme.typography.titleMedium)
        Text("LFR Community v1.0.0", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Compose Multiplatform", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
