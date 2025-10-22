package com.example.nav3recipes.modular.koin

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import com.example.nav3recipes.modular.hilt.EntryProviderInstaller
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.dsl.module

val appModule = module {

    activityRetainedScope {
        scoped {
            Navigator(startDestination = ConversationList)
        }

        scoped {
            val navigator = get<Navigator>()
            val navigationSet = hashSetOf<EntryProviderInstaller>()
            navigationSet.add {
                entry<ConversationList> {
                    //TODO make it private
                    ConversationListScreen(
                        onConversationClicked = { conversationDetail ->
                            navigator.goTo(conversationDetail)
                        }
                    )
                }
            }
            navigationSet.add {
                entry<ConversationDetail> { key ->
                    //TODO make it private
                    ConversationDetailScreen(key) { navigator.goTo(Profile) }
                }
            }
            navigationSet.add {
                entry<Profile>{
                    //TODO make it private
                    ProfileScreen()
                }
            }
            navigationSet
        }

        scoped<(Any) -> NavEntry<Any>> {
            val navigationSet = get<HashSet<EntryProviderInstaller>>()
            entryProvider { navigationSet.forEach { builder -> this.builder() } }
        }
    }
}