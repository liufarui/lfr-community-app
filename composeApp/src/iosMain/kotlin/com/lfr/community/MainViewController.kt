package com.lfr.community

import androidx.compose.ui.window.ComposeUIViewController
import com.lfr.community.data.auth.IosTokenStore

fun MainViewController() = ComposeUIViewController { App(IosTokenStore()) }
