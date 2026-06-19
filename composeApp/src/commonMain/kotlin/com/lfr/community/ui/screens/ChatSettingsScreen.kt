package com.lfr.community.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lfr.community.data.model.GroupChat
import com.lfr.community.data.model.Member
import com.lfr.community.data.repository.CommunityRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatSettingsScreen(
    repository: CommunityRepository,
    chatId: String,
    navController: NavController
) {
    var chat by remember { mutableStateOf<GroupChat?>(null) }
    var allMembers by remember { mutableStateOf<Map<String, Member>>(emptyMap()) }
    var editName by remember { mutableStateOf("") }
    var showAddMember by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(chatId) {
        val chats = repository.getGroupChats()
        chat = chats.find { it.id == chatId }
        allMembers = repository.getMembers().associateBy { it.id }
        editName = chat?.name ?: ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("群聊设置") },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("← 返回")
                    }
                }
            )
        }
    ) { padding ->
        chat?.let { c ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 群名修改
                item {
                    Text("群聊名称", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                        Button(
                            onClick = {
                                scope.launch {
                                    val updated = repository.updateGroupChat(chatId, name = editName.trim())
                                    if (updated != null) chat = updated
                                }
                            },
                            enabled = editName.trim() != c.name && editName.isNotBlank()
                        ) { Text("保存") }
                    }
                }

                // 群头像 (emoji 选择)
                item {
                    Text("群聊图标", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(4.dp))
                    val emojis = listOf("💬", "🏃", "📚", "🎮", "🎵", "🍜", "🌟", "🔥", "🌈", "🐱", "☕", "🎯")
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        emojis.take(6).forEach { e ->
                            Surface(
                                modifier = Modifier.size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        scope.launch {
                                            val updated = repository.updateGroupChat(chatId, emoji = e)
                                            if (updated != null) chat = updated
                                        }
                                    },
                                color = if (c.emoji == e) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Box(contentAlignment = Alignment.Center) { Text(e, style = MaterialTheme.typography.titleMedium) }
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        emojis.drop(6).forEach { e ->
                            Surface(
                                modifier = Modifier.size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        scope.launch {
                                            val updated = repository.updateGroupChat(chatId, emoji = e)
                                            if (updated != null) chat = updated
                                        }
                                    },
                                color = if (c.emoji == e) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Box(contentAlignment = Alignment.Center) { Text(e, style = MaterialTheme.typography.titleMedium) }
                            }
                        }
                    }
                }

                // 成员列表
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("成员 (${c.members.size}人)", style = MaterialTheme.typography.labelMedium)
                        TextButton(onClick = { showAddMember = true }) { Text("+ 添加") }
                    }
                }

                items(c.members) { memberId ->
                    val member = allMembers[memberId]
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(member?.emoji ?: "👤", style = MaterialTheme.typography.titleMedium)
                        Text(
                            member?.displayName ?: member?.name ?: memberId,
                            modifier = Modifier.weight(1f)
                        )
                        if (memberId == c.creator) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text("创建者", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall)
                            }
                        } else {
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        val updated = repository.removeMember(chatId, memberId)
                                        if (updated != null) chat = updated
                                    }
                                },
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) { Text("移除") }
                        }
                    }
                }
            }
        } ?: Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    // 添加成员对话框
    if (showAddMember && chat != null) {
        val nonMembers = allMembers.values.filter { it.id !in chat!!.members }
        AlertDialog(
            onDismissRequest = { showAddMember = false },
            title = { Text("添加成员") },
            text = {
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(nonMembers) { member ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clickable {
                                    scope.launch {
                                        val updated = repository.addMembers(chatId, listOf(member.id))
                                        if (updated != null) chat = updated
                                        showAddMember = false
                                    }
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(member.emoji)
                            Text(member.displayName.ifEmpty { member.name })
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddMember = false }) { Text("关闭") }
            }
        )
    }
}
