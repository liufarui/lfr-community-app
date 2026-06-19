package com.lfr.community.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lfr.community.data.model.Member

@Composable
fun CreateChatDialog(
    members: List<Member>,
    onDismiss: () -> Unit,
    onCreate: (name: String, emoji: String, members: List<String>) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("💬") }
    var selected by remember { mutableStateOf(setOf<String>()) }

    val emojiOptions = listOf("💬", "🏃", "📚", "🎮", "🎵", "🍜", "🌟", "🔥", "🌈", "🐱", "☕", "🎯")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新建群聊") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("群聊名称") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Text("选择图标", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    emojiOptions.take(6).forEach { e ->
                        Surface(
                            modifier = Modifier.size(36.dp).clickable { emoji = e },
                            shape = RoundedCornerShape(8.dp),
                            color = if (emoji == e) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Box(contentAlignment = Alignment.Center) { Text(e) }
                        }
                    }
                }
                Text("选择成员 (${selected.size}人)", style = MaterialTheme.typography.labelMedium)
                LazyColumn(modifier = Modifier.height(200.dp)) {
                    items(members) { member ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable {
                                selected = if (member.id in selected) selected - member.id
                                           else selected + member.id
                            }.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Checkbox(checked = member.id in selected, onCheckedChange = null)
                            Text(member.emoji)
                            Text(member.displayName.ifEmpty { member.name })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(name, emoji, selected.toList()) },
                enabled = name.isNotBlank() && selected.isNotEmpty()
            ) { Text("创建") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
