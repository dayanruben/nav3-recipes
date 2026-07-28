package com.example.nav3recipes.deeplink.usecases.matcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.invoke
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.nav3recipes.common.deeplink.EntryScreen
import com.example.nav3recipes.common.deeplink.TextContent
import com.example.nav3recipes.ui.setEdgeToEdgeConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable
internal data class HomeKey(val name: String): NavKey

@Serializable
internal object FallbackKey: NavKey

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setEdgeToEdgeConfig()
        super.onCreate(savedInstanceState)

        val request = DeepLinkRequest(intent)
        val deepLinkMatcher = createJsonDeepLinkMatcher<HomeKey>()

        val matchResult = deepLinkMatcher.match(request)
        val key = matchResult?.key ?: FallbackKey

        setContent {
            val backStack: NavBackStack<NavKey> = rememberNavBackStack(key)
            NavDisplay(
                backStack = backStack,
                onBack = backStack::removeLastOrNull,
                entryProvider = entryProvider {
                    entry<HomeKey> { key ->
                        EntryScreen("Welcome") {
                            TextContent(key.name)
                        }
                    }
                    entry<FallbackKey> { key ->
                        EntryScreen("Fallback Key") {
                            TextContent(
                                "Failed to deep link - DeepLinkRequest " +
                                        "did not match with any DeepLinkMatcher"
                            )
                        }
                    }
                }
            )
        }
    }
}

// Optional JsonDeepLinkMatcher factory function that automatically captures KSerializer for T.
private inline fun <reified T : NavKey> createJsonDeepLinkMatcher(): JsonDeepLinkMatcher<T> {
    val serializer = serializer<T>()
    return JsonDeepLinkMatcher(serializer)
}
