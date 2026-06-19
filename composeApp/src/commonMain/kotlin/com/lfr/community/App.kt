package com.lfr.community

import androidx.compose.runtime.Composable
import com.lfr.community.data.auth.InMemoryTokenStore
import com.lfr.community.data.auth.TokenStore
import com.lfr.community.data.repository.CommunityRepository
import com.lfr.community.navigation.AppNavigation
import com.lfr.community.ui.theme.CommunityTheme

@Composable
fun App(tokenStore: TokenStore = InMemoryTokenStore()) {
    val repository = CommunityRepository(tokenStore = tokenStore)
    CommunityTheme {
        AppNavigation(repository)
    }
}
