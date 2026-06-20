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
import androidx.compose.ui.text.style.TextAlign
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
    var hasMore by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val pageSize = 50

    fun loadData(isRefresh: Boolean = false) {
        scope.launch {
            try {
                if (isRefresh) {
                    refreshing = true
                } else {
                    loading = true
                }
                val newMessages = repository.getMessages(limit = pageSize, offset = 0)
                members = repository.getMembers().associateBy { it.id }
                messages = newMessages
                hasMore = newMessages.size >= pageSize
            } catch (_: Exception) {
                // Repository 已处理异常，这里只确保状态重置
            } finally {
                loading = false
                refreshing = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem >= messages.size - 5 && !loadingMore && !refreshing && hasMore
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && messages.isNotEmpty()) {
            try {
                loadingMore = true
                val moreMessages = repository.getMessages(limit = pageSize, offset = messages.size)
                if (moreMessages.isNotEmpty()) {
                    val existingIds = messages.map { it.id }.toSet()
                    val newMessages = moreMessages.filter { it.id !in existingIds }
                    messages = messages + newMessages
                    hasMore = newMessages.isNotEmpty() && moreMessages.size >= pageSize
                } else {
                    hasMore = false
                }
            } catch (_: Exception) {
                // 加载失败时保持 hasMore 为 true，允许重试
            } finally {
                loadingMore = false
            }
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
                    } else if (!hasMore && messages.isNotEmpty()) {
                        item {
                            Text(
                                "没有更多了",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
