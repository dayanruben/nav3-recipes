package com.example.nav3recipes.modular.koin

import android.content.Intent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.BackStackMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.invoke

class Navigator(
    startDestination: NavKey,
    private val deepLinkMatchers: List<BackStackMatcher<*, NavKey>>,
) {
    val backStack: SnapshotStateList<NavKey> = mutableStateListOf(startDestination)

    fun goTo(destination: NavKey) {
        backStack.add(destination)
    }

    fun handleDeepLink(intent: Intent) {
        if (intent.data == null && intent.extras == null) return

        val request = DeepLinkRequest(intent)
        val matchResult = deepLinkMatchers.firstNotNullOfOrNull { matcher ->
            matcher.match(request)
        } ?: return

        backStack.clear()
        backStack.addAll(matchResult.backStack)
    }

    fun goBack() {
        backStack.removeLastOrNull()
    }
}