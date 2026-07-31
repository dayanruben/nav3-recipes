package com.example.nav3recipes.deeplink.handlerequests.staticuri

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.nav3recipes.common.deeplink.EntryScreen
import com.example.nav3recipes.common.deeplink.TextContent
import com.example.nav3recipes.deeplink.handlerequests.basic.HomeKey
import com.example.nav3recipes.deeplink.handlerequests.basic.NavRecipeKey
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.invoke
import com.example.nav3recipes.ui.setEdgeToEdgeConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer


@Serializable
internal object FallbackKey: NavRecipeKey {
    override val name: String = "Fallback Key"
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setEdgeToEdgeConfig()
        super.onCreate(savedInstanceState)

        // create a DeepLinkRequest with the intent
        val request = DeepLinkRequest(intent)

        // try to match DeepLinkRequest to a DeepLinkMatcher
        val matchResult = HOME_MATCHER.match(request)
        val key = matchResult?.key ?: FallbackKey

        /**
         * Then pass starting key to backstack
         */
        setContent {
            val backStack: NavBackStack<NavKey> = rememberNavBackStack(key)
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = entryProvider {
                    entry<HomeKey> { key ->
                        EntryScreen(key.name) {
                            TextContent("Deep linked to Home")
                        }
                    }
                    entry<FallbackKey> { key ->
                        EntryScreen("${key.name} ") {
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

/**
 * Each matcher is associated with a navigation key that supports this deep link.
 *
 * A navigation key can be associated with multiple DeepLinkMatchers if it supports more than one deep link.
 */
private val HOME_MATCHER = UriDeepLinkMatcher(
    uriPattern = DeepLinkUri(HOME_URI),
    serializer = serializer<HomeKey>(),
)