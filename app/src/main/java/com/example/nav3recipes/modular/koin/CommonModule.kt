package com.example.nav3recipes.modular.koin

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderBuilder
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject


typealias EntryProviderInstaller = EntryProviderBuilder<Any>.() -> Unit

class Navigator(startDestination: Any) {
    val backStack : SnapshotStateList<Any> = mutableStateListOf(startDestination)

    fun goTo(destination: Any){
        backStack.add(destination)
    }

    fun goBack(){
        backStack.removeLastOrNull()
    }
}