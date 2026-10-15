package com.example.nav3recipes.modular.metro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.BackStackMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.withBackStack
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import kotlinx.serialization.Serializable

// API
@Serializable
object Profile : NavKey

const val PROFILE_URI = "https://www.nav3recipes.com/profile"

// IMPLEMENTATION
@ContributesTo(ActivityScope::class)
@BindingContainer
object ProfileModule {

    @IntoSet
    @Provides
    fun provideEntryProviderInstaller() : EntryProviderInstaller = {
        entry<Profile>{
            ProfileScreen()
        }
    }

    @IntoSet
    @Provides
    fun provideDeepLinkMatcher(): BackStackMatcher<*, NavKey> =
        UriDeepLinkMatcher(
            uriPattern = DeepLinkUri(PROFILE_URI),
            serializer = Profile.serializer(),
        ).withBackStack { matchResult ->
            listOf(ConversationList, matchResult.key)
        }
}

@Composable
private fun ProfileScreen() {
    val profileColor = MaterialTheme.colorScheme.surfaceVariant
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(profileColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Profile Screen",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
