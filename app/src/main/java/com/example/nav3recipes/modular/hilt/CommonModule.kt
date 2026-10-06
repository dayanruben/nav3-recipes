package com.example.nav3recipes.modular.hilt

import android.content.Intent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.BackStackMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.invoke
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

typealias EntryProviderInstaller = EntryProviderScope<NavKey>.() -> Unit

@ActivityRetainedScoped
class Navigator @Inject constructor(
    startDestination: NavKey,
    private val deepLinkMatchers: Set<@JvmSuppressWildcards BackStackMatcher<*, NavKey>>,
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