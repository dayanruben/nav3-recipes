package com.example.nav3recipes.deeplink.handlerequests.uriarguments

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.net.toUri
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.invoke
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.nav3recipes.common.deeplink.EntryScreen
import com.example.nav3recipes.common.deeplink.FriendsList
import com.example.nav3recipes.common.deeplink.LIST_USERS
import com.example.nav3recipes.common.deeplink.TextContent
import com.example.nav3recipes.deeplink.handlerequests.uriarguments.ui.URL_HOME_EXACT
import com.example.nav3recipes.deeplink.handlerequests.uriarguments.ui.URL_SEARCH
import com.example.nav3recipes.deeplink.handlerequests.uriarguments.ui.URL_USERS_WITH_FILTER
import com.example.nav3recipes.ui.setEdgeToEdgeConfig
import kotlinx.serialization.serializer

/**
 * See README.md for how this recipe works.
 */
class MainActivity : ComponentActivity() {
    /** STEP 1. Declare supported deep links */
    internal val deepLinkMatchers: List<UriDeepLinkMatcher<NavKey>> = listOf(
        // "https://www.nav3recipes.com/home"
        UriDeepLinkMatcher(URL_HOME_EXACT.toUri(), serializer<HomeKey>()),
        // "https://www.nav3recipes.com/users/with/{filter}"
        UriDeepLinkMatcher(URL_USERS_WITH_FILTER.toUri(), serializer<UsersKey>()),
        // "https://www.nav3recipes.com/users/search?{firstName}&{age}&{location}"
        UriDeepLinkMatcher(URL_SEARCH.toUri(), serializer<SearchKey>()),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        setEdgeToEdgeConfig()
        super.onCreate(savedInstanceState)

        /** STEP 2. Create a [DeepLinkRequest] from the intent */
        val request = DeepLinkRequest(intent)

        /** STEP 3. Match the request to the DeepLinkMatchers*/
        // First get all the possible matching UriMatchResult
        val matches = deepLinkMatchers.mapNotNull {
            // returns null if no match
            it.match(request)
        }
        // compare all matches to find best match
        val bestMatch = matches.maxOrNull()
        /** STEP 4. Get the key from the match or use default key if no match*/
        val key = bestMatch?.key ?: HomeKey

        /**
         * STEP 5. pass the initial key to backstack
         */
        setContent {
            val backStack: NavBackStack<NavKey> = rememberNavBackStack(key)
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = entryProvider {
                    entry<HomeKey> { key ->
                        EntryScreen(key.name) {
                            TextContent("<matches exact url>")
                        }
                    }
                    entry<UsersKey> { key ->
                        EntryScreen("${key.name} : ${key.filter}") {
                            TextContent("<matches path argument>")
                            val list = when {
                                key.filter.isEmpty() -> LIST_USERS
                                key.filter == UsersKey.FILTER_OPTION_ALL -> LIST_USERS
                                else -> LIST_USERS.take(5)
                            }
                            FriendsList(list)
                        }
                    }
                    entry<SearchKey> { search ->
                        EntryScreen(search.name) {
                            TextContent("<matches query parameters, if any>")
                            val matchingUsers = LIST_USERS.filter { user ->
                                (search.firstName == null || user.firstName == search.firstName) &&
                                        (search.location == null || user.location == search.location) &&
                                        (search.ageMin == null || user.age >= search.ageMin) &&
                                        (search.ageMax == null || user.age <= search.ageMax)
                            }
                            FriendsList(matchingUsers)
                        }
                    }
                }
            )
        }
    }
}