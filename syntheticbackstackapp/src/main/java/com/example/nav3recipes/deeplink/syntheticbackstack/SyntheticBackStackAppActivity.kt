package com.example.nav3recipes.deeplink.syntheticbackstack

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.invoke
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.nav3recipes.common.deeplink.EntryScreen
import com.example.nav3recipes.common.deeplink.FriendsList
import com.example.nav3recipes.common.deeplink.LIST_USERS
import com.example.nav3recipes.common.deeplink.PaddedButton
import com.example.nav3recipes.deeplink.syntheticbackstack.util.SimpleDeepLinkMatcher
import com.example.nav3recipes.deeplink.syntheticbackstack.util.navigateUp
import com.example.nav3recipes.deeplink.syntheticbackstack.util.withBackStack

class SyntheticBackStackAppActivity: ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val request = DeepLinkRequest(intent)

        val flags = intent.flags
        val isNewTask = flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0 &&
                flags and Intent.FLAG_ACTIVITY_CLEAR_TASK != 0

        val matcher = SimpleDeepLinkMatcher().withBackStack(isNewTask)
        val backStack = matcher.match(request)?.backStack ?: listOf(Home)

        setContent {
            val backStack: NavBackStack<NavKey> = rememberNavBackStack().apply {
                addAll(backStack)
            }

            Scaffold(
                topBar = {
                    // top app bar to display up button
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { stringResource(R.string.app_name)},
                        navigationIcon = {
                            /**
                             * Up button should never exit your app. Do not display it
                             * on the root Screen.
                             */
                            if (backStack.last() != Home) {
                                IconButton(onClick = {
                                    backStack.navigateUp(
                                        this@SyntheticBackStackAppActivity,
                                        this@SyntheticBackStackAppActivity
                                    )
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.outline_arrow_upward_24),
                                        contentDescription = "Up Button",
                                    )
                                }
                            }
                        },
                    )
                },
            ) { innerPadding ->
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull()},
                    modifier = Modifier.padding(innerPadding),
                    entryProvider = entryProvider {
                        entry<Home> { key ->
                            EntryScreen(key.screenTitle) {
                                PaddedButton("See Users") {
                                    backStack.add(Users)
                                }
                            }
                        }
                        entry<Users> { key ->
                            EntryScreen(key.screenTitle) {
                                FriendsList(LIST_USERS) { user ->
                                    backStack.add(UserDetail(user))
                                }
                            }
                        }
                        entry<UserDetail> { result ->
                            EntryScreen(result.screenTitle) {
                                FriendsList(listOf(result.user))
                            }
                        }
                    }
                )
            }
        }
    }
}