package com.lfr.community.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lfr.community.data.model.GroupChat
import com.lfr.community.data.model.Member
import com.lfr.community.data.model.Message
import com.lfr.community.data.repository.CommunityRepository
import com.lfr.community.ui.components.MessageBubble
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(repository: CommunityRepository, chatId: String, navController: NavController) {
    var chat by remember { mutableStateOf<GroupChat?>(null) }
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var members by remember { mutableStateOf<Map<String, Member>>(emptyMap()) }
    var inputText by remember { mutableStateOf("") }
    var sending by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(chatId) {
        scope.launch {
            val chats = repository.getGroupChats()
            chat = chats.find { it.id == chatId }
            messages = repository.getChatMessages(chatId)
            members = repository.getMembers().associateBy { it.id }
            if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(chat?.name ?: "...", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${chat?.members?.size ?: 0}人",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { navController.navigate("chats/${chatId}/settings") }) {
                        Text("⚙️")
                    }
                },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("← 返回")
                    }
                },
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("输入消息...") },
                    maxLines = 3,
                )
                Button(
                    onClick = {
                        if (inputText.isNotBlank() && !sending) {
                            sending = true
                            scope.launch {
                                repository.sendChatMessage(chatId, inputText.trim())
                                inputText = ""
                                messages = repository.getChatMessages(chatId)
                                sending = false
                                if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
                            }
                        }
                    },
                    enabled = inputText.isNotBlank() && !sending
                ) {
                    Text("发送")
                }
            }
        }
    ) { padding ->
        if (messages.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("暂无消息，发送第一条吧", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    MessageBubble(msg, members[msg.author])
                }
            }
        }
    }
}
