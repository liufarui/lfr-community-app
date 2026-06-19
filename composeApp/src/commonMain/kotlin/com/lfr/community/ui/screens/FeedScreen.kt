package com.lfr.community.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(repository: CommunityRepository) {
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var members by remember { mutableStateOf<Map<String, Member>>(emptyMap()) }
    var loading by remember { mutableStateOf(true) }
    var refreshing by remember { mutableStateOf(false) }
    var loadingMore by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val pageSize = 50

    fun loadData(isRefresh: Boolean = false) {
        scope.launch {
            if (isRefresh) {
                refreshing = true
                messages = repository.getMessages(pageSize)
                members = repository.getMembers().associateBy { it.id }
                refreshing = false
            } else if (loading) {
                messages = repository.getMessages(pageSize)
                members = repository.getMembers().associateBy { it.id }
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem >= messages.size - 5 && !loadingMore && !refreshing
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && messages.isNotEmpty()) {
            loadingMore = true
            val moreMessages = repository.getMessages(messages.size + pageSize)
            messages = moreMessages
            loadingMore = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        MockDataBanner(repository.isUsingMock)
        Text(
            "广场",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        PullToRefreshBox(
            isRefreshing = refreshing,
            onRefresh = { loadData(isRefresh = true) },
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                messages.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("暂无消息", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                else -> LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        MessageBubble(msg, members[msg.author])
                    }
                    if (loadingMore) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
