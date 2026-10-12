package com.example.nav3recipes.modular.metro

import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(ActivityScope::class)
@BindingContainer
object AppModule {

    @Provides
    fun provideStartDestination(): NavKey = ConversationList
}
