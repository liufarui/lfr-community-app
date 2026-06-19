package com.lfr.community.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lfr.community.data.model.Member
import com.lfr.community.data.model.Message
import com.lfr.community.data.repository.CommunityRepository
import com.lfr.community.ui.components.MessageBubble
import com.lfr.community.ui.components.MockDataBanner
import kotlinx.coroutines.launch

@Composable
fun FeedScreen(repository: CommunityRepository) {
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var members by remember { mutableStateOf<Map<String, Member>>(emptyMap()) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            val (msgs, mbrs) = Pair(repository.getMessages(), repository.getMembers())
            messages = msgs
            members = mbrs.associateBy { it.id }
            loading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        MockDataBanner(repository.isUsingMock)
        Text(
            "广场",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (messages.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("暂无消息", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
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
