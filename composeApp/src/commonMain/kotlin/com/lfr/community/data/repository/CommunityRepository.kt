package com.lfr.community.data.repository

import com.lfr.community.data.api.*
import com.lfr.community.data.auth.InMemoryTokenStore
import com.lfr.community.data.auth.TokenStore
import com.lfr.community.data.model.*

class CommunityRepository(
    tokenStore: TokenStore = InMemoryTokenStore(),
    private val api: CommunityApi = CommunityApi(tokenStore = tokenStore),
) {

    var isUsingMock = false
        private set

    fun setMockMode(enabled: Boolean) {
        isUsingMock = enabled
    }

    // === Members ===

    suspend fun getMembers(): List<Member> = try {
        api.fetchMembers().also { isUsingMock = false }
    } catch (_: Exception) {
        isUsingMock = true
        MockCommunityApi.members
    }

    // === Feed Messages ===

    suspend fun getMessages(limit: Int = 50): List<Message> = try {
        api.fetchMessages(limit).also { isUsingMock = false }
    } catch (_: Exception) {
        isUsingMock = true
        MockCommunityApi.messages.take(limit)
    }

    // === Group Chats ===

    suspend fun getGroupChats(): List<GroupChat> = try {
        api.fetchGroupChats().also { isUsingMock = false }
    } catch (_: Exception) {
        isUsingMock = true
        MockCommunityApi.groupChats
    }

    suspend fun createGroupChat(name: String, emoji: String, members: List<String>): GroupChat? =
        try { api.createGroupChat(name, emoji, members) } catch (_: Exception) { null }

    suspend fun updateGroupChat(chatId: String, name: String? = null, emoji: String? = null): GroupChat? =
        try { api.updateGroupChat(chatId, name, emoji) } catch (_: Exception) { null }

    suspend fun addMembers(chatId: String, members: List<String>): GroupChat? =
        try { api.addGroupChatMembers(chatId, members) } catch (_: Exception) { null }

    suspend fun removeMember(chatId: String, memberId: String): GroupChat? =
        try { api.removeGroupChatMember(chatId, memberId) } catch (_: Exception) { null }

    suspend fun getChatMessages(chatId: String): List<Message> = try {
        api.fetchGroupChatMessages(chatId).also { isUsingMock = false }
    } catch (_: Exception) {
        isUsingMock = true
        MockCommunityApi.chatMessages(chatId)
    }

    suspend fun sendChatMessage(chatId: String, content: String): Boolean =
        try { api.postGroupChatMessage(chatId, content); true } catch (_: Exception) { false }

    // === Auth ===

    suspend fun login(username: String, password: String): Boolean =
        try { api.auth.login(username, password); true } catch (_: Exception) { false }

    suspend fun logout() = api.auth.logout()

    suspend fun isLoggedIn(): Boolean = api.auth.isLoggedIn()

    // === Topics ===

    suspend fun getTopics(status: String? = null, category: String? = null): List<Topic> =
        try { api.topics.list(status, category) } catch (_: Exception) { emptyList() }

    suspend fun getTopic(id: String): Topic? =
        try { api.topics.get(id) } catch (_: Exception) { null }

    suspend fun createTopic(title: String, description: String? = null, category: String? = null, priority: String? = null): Topic? =
        try { api.topics.create(CreateTopicRequest(title, description, category, priority)) } catch (_: Exception) { null }

    suspend fun voteTopic(id: String, vote: VoteType): TopicVotes? =
        try { api.topics.vote(id, vote) } catch (_: Exception) { null }

    suspend fun getTopicComments(id: String): List<Comment> =
        try { api.topics.listComments(id) } catch (_: Exception) { emptyList() }

    suspend fun addTopicComment(id: String, content: String): Comment? =
        try { api.topics.addComment(id, content) } catch (_: Exception) { null }

    suspend fun closeTopic(id: String, reason: String? = null): Topic? =
        try { api.topics.close(id, reason) } catch (_: Exception) { null }

    // === Agents ===

    suspend fun getAgents(): List<Agent> =
        try { api.agents.list() } catch (_: Exception) { emptyList() }

    // === Threads ===

    suspend fun getThreadsByRoom(roomId: String): List<ThreadMeta> =
        try { api.threads.getByRoom(roomId) } catch (_: Exception) { emptyList() }

    suspend fun getThreadMessages(threadId: String): List<Message> =
        try { api.threads.getMessages(threadId) } catch (_: Exception) { emptyList() }

    suspend fun createThread(title: String, room: String, firstMessage: String? = null): ThreadMeta? =
        try { api.threads.create(title, room, firstMessage) } catch (_: Exception) { null }

    suspend fun postThreadMessage(threadId: String, content: String): Boolean =
        try { api.threads.post(threadId, content); true } catch (_: Exception) { false }

    // === Config ===

    fun updateApiBase(url: String) { api.updateBaseUrl(url) }
}
