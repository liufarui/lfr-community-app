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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lfr.community.data.model.GroupChat
import com.lfr.community.data.model.Member
import com.lfr.community.data.repository.CommunityRepository
import com.lfr.community.ui.components.CreateChatDialog
import com.lfr.community.ui.components.MockDataBanner
import kotlinx.coroutines.launch

@Composable
fun ChatListScreen(repository: CommunityRepository, navController: NavController) {
    var chats by remember { mutableStateOf<List<GroupChat>>(emptyList()) }
    var members by remember { mutableStateOf<Map<String, Member>>(emptyMap()) }
    var loading by remember { mutableStateOf(true) }
    var showCreate by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            chats = repository.getGroupChats()
            members = repository.getMembers().associateBy { it.id }
        } catch (_: Exception) {
            // Repository 已处理异常
        } finally {
            loading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        MockDataBanner(repository.isUsingMock)
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("群聊", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = { showCreate = true }) {
                Text("+ 新建")
            }
        }

        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (chats.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("还没有群聊，点击右上角创建", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(chats, key = { it.id }) { chat ->
                    ChatItem(chat, members) { navController.navigate("chats/${chat.id}") }
                }
            }
        }
    }

    if (showCreate) {
        CreateChatDialog(
            members = members.values.filter { it.id != "hanshu" },
            onDismiss = { showCreate = false },
            onCreate = { name, emoji, selectedMembers ->
                scope.launch {
                    repository.createGroupChat(name, emoji, selectedMembers)
                    chats = repository.getGroupChats()
                    showCreate = false
                }
            }
        )
    }
}

@Composable
private fun ChatItem(chat: GroupChat, members: Map<String, Member>, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(chat.emoji, style = MaterialTheme.typography.headlineMedium)
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(chat.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    "${chat.members.size}人",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            chat.lastMessage?.let { msg ->
                val authorName = members[msg.author]?.displayName ?: msg.author
                Text(
                    "$authorName: ${msg.content}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } ?: Text(
                "暂无消息",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    HorizontalDivider(modifier = Modifier.padding(start = 76.dp))
}
