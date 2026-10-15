package com.example.nav3recipes.modular.metro

import android.content.Intent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.BackStackMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.invoke
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

typealias EntryProviderInstaller = EntryProviderScope<NavKey>.() -> Unit

@SingleIn(ActivityScope::class)
@Inject
class Navigator(
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