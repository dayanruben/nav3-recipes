package com.example.nav3recipes.modular.koin

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entry
import com.example.nav3recipes.modular.hilt.EntryProviderInstaller
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module
import org.koin.core.module._scopedInstanceFactory
import org.koin.core.module._singleInstanceFactory
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL
import org.koin.dsl.module

val appModule = module {
    includes(profileModule,conversationModule)

    scope<KoinModularActivity> {
        scoped {
            Navigator(startDestination = ConversationList)
        }
    }
}

@KoinDslMarker
fun Scope.navigator(): Navigator = get<Navigator>()

//TODO Need typealias EntryProviderInstaller = EntryProviderBuilder<Any>.() -> Unit-

@KoinDslMarker
@OptIn(KoinInternalApi::class)
inline fun <reified T : Any> ScopeDSL.navigation(
    noinline definition: @Composable Scope.(T) -> Unit,
): KoinDefinition<EntryProviderInstaller> {
    val def = _scopedInstanceFactory<EntryProviderInstaller>(named<T>(), {
        val scope = this
        {
            entry<T>(content = {definition(scope,it)})
        }
    }, scopeQualifier)
    module.indexPrimaryType(def)
    return KoinDefinition(module, def)
}

@KoinDslMarker
@OptIn(KoinInternalApi::class)
inline fun <reified T : Any> Module.navigation(
    noinline definition: @Composable Scope.(T) -> Unit,
): KoinDefinition<EntryProviderInstaller> {
    val def = _singleInstanceFactory<EntryProviderInstaller>(named<T>(), {
        val scope = this
        {
            entry<T>(content = {definition(scope,it)})
        }
    })
    indexPrimaryType(def)
    return KoinDefinition(this, def)
}