package com.lfr.community.data.repository

import com.lfr.community.data.api.CommunityApi
import com.lfr.community.data.model.*

class CommunityRepository(private val api: CommunityApi = CommunityApi()) {

    suspend fun getMembers(): List<Member> = try { api.fetchMembers() } catch (_: Exception) { emptyList() }

    suspend fun getMessages(limit: Int = 50): List<Message> = try { api.fetchMessages(limit) } catch (_: Exception) { emptyList() }

    suspend fun getGroupChats(): List<GroupChat> = try { api.fetchGroupChats() } catch (_: Exception) { emptyList() }

    suspend fun createGroupChat(name: String, emoji: String, members: List<String>): GroupChat? =
        try { api.createGroupChat(name, emoji, members) } catch (_: Exception) { null }

    suspend fun updateGroupChat(chatId: String, name: String? = null, emoji: String? = null): GroupChat? =
        try { api.updateGroupChat(chatId, name, emoji) } catch (_: Exception) { null }

    suspend fun addMembers(chatId: String, members: List<String>): GroupChat? =
        try { api.addGroupChatMembers(chatId, members) } catch (_: Exception) { null }

    suspend fun removeMember(chatId: String, memberId: String): GroupChat? =
        try { api.removeGroupChatMember(chatId, memberId) } catch (_: Exception) { null }

    suspend fun getChatMessages(chatId: String): List<Message> =
        try { api.fetchGroupChatMessages(chatId) } catch (_: Exception) { emptyList() }

    suspend fun sendChatMessage(chatId: String, content: String): Boolean =
        try { api.postGroupChatMessage(chatId, content); true } catch (_: Exception) { false }

    fun updateApiBase(url: String) { api.updateBaseUrl(url) }
}
