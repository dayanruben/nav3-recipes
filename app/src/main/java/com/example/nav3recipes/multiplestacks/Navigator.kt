/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.nav3recipes.multiplestacks

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.serialization.decodeFromSavedState
import androidx.savedstate.serialization.encodeToSavedState
import androidx.savedstate.write
import kotlinx.serialization.Serializable

/**
 * Create a Navigator that is saved and restored through config changes and process death.
 */
@Composable
fun rememberNavigator(
    startRoute: Route,
    topLevelRoutes: Set<Route>,
) : Navigator {

    return rememberSaveable(saver = Navigator.Saver) {
        Navigator(
            startRoute = startRoute,
            topLevelRoutes = topLevelRoutes,
        )
    }
}

/**
 * Navigator object that manages navigation state and provides `NavEntry`s for that state.
 */
@SuppressLint("RestrictedApi")
class Navigator private constructor(
    val startRoute: Route,
    initialTopLevelRoute: Route,
    initialTopLevelStacks: Map<Route, List<Route>>
) {

    constructor(
        startRoute: Route,
        topLevelRoutes: Set<Route>
    ) : this(
        startRoute = startRoute,
        initialTopLevelRoute = startRoute,
        initialTopLevelStacks = topLevelRoutes.associateWith { route -> listOf(route) }
    )

    var topLevelRoute by mutableStateOf(initialTopLevelRoute)
        private set

    // Maintain a stack for each top level route
    val topLevelStacks : Map<Route, SnapshotStateList<Route>> =
        initialTopLevelStacks.mapValues { (_, values) -> values.toMutableStateList() }

    /**
     * Navigate to the given route.
     */
    fun navigate(route: Route) {
        if (route is Route.TopLevel) {
            topLevelRoute = route
        } else {
            topLevelStacks[topLevelRoute]?.add(route)
        }
    }

    /**
     * Go back to the previous route.
     */
    fun goBack() {

        val currentStack = topLevelStacks[topLevelRoute] ?:
            error("Stack for $topLevelRoute not found")
        val currentRoute = currentStack.last()

        // If we're at the base of the current route, go back to the start route stack.
        if (currentRoute == topLevelRoute){
            topLevelRoute = startRoute
        } else {
            currentStack.removeLast()
        }
    }

    /**
     * Get the NavEntries for the current navigation state.
     */
    @Composable
    fun entries(
        entryProvider: (Route) -> NavEntry<Route>
    ): SnapshotStateList<NavEntry<Route>> {

        val decoratedEntries = topLevelStacks.mapValues { (_, stack) ->

            val decorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator<Route>(),
            )

            rememberDecoratedNavEntries(
                backStack = stack,
                entryDecorators = decorators,
                entryProvider = entryProvider
            )
        }

        val stacksToUse = mutableListOf(startRoute)
        if (topLevelRoute != startRoute) stacksToUse += topLevelRoute

        return stacksToUse
            .flatMap { decoratedEntries[it] ?: emptyList() }
            .toMutableStateList()
    }

    companion object {
        private const val KEY_START_ROUTE = "start_route"
        private const val KEY_TOP_LEVEL_ROUTE = "top_level_route"
        private const val KEY_TOP_LEVEL_STACK_IDS = "top_level_stack_ids"
        private const val KEY_TOP_LEVEL_STACK_KEY_PREFIX = "top_level_stack_key_"
        private const val KEY_TOP_LEVEL_STACK_VALUES_PREFIX = "top_level_stack_values_"

        val Saver = Saver<Navigator, SavedState>(
            save = { navigator ->
                val savedState = SavedState()
                savedState.write {
                    putSavedState(KEY_START_ROUTE, encodeToSavedState(navigator.startRoute))
                    putSavedState(KEY_TOP_LEVEL_ROUTE, encodeToSavedState(navigator.topLevelRoute))

                    var id = 0
                    val ids = mutableListOf<Int>()

                    for ((key, stackValues) in navigator.topLevelStacks) {
                        putSavedState("$KEY_TOP_LEVEL_STACK_KEY_PREFIX$id", encodeToSavedState(key))
                        putSavedStateList(
                            "$KEY_TOP_LEVEL_STACK_VALUES_PREFIX$id",
                            stackValues.map { encodeToSavedState(it) })
                        ids.add(id)
                        id++
                    }

                    putIntList(KEY_TOP_LEVEL_STACK_IDS, ids)
                }
                savedState
            },
            restore = { savedState ->
                savedState.read {
                    val restoredStartRoute =
                        decodeFromSavedState<Route>(getSavedState(KEY_START_ROUTE))
                    val restoredTopLevelRoute =
                        decodeFromSavedState<Route>(getSavedState(KEY_TOP_LEVEL_ROUTE))

                    val topLevelRoutes = mutableSetOf<Route>()
                    val topLevelStacks = mutableMapOf<Route, List<Route>>()
                    
                    val ids = getIntList(KEY_TOP_LEVEL_STACK_IDS)
                    for (id in ids) {
                        // get the top level key and the keys on the stack
                        val key: Route =
                            decodeFromSavedState(getSavedState("$KEY_TOP_LEVEL_STACK_KEY_PREFIX$id"))
                        topLevelRoutes.add(key)
                        topLevelStacks[key] = getSavedStateList("$KEY_TOP_LEVEL_STACK_VALUES_PREFIX$id")
                            .map { decodeFromSavedState<Route>(it) }
                    }
                    
                    Navigator(
                        startRoute = restoredStartRoute,
                        initialTopLevelRoute = restoredTopLevelRoute,
                        initialTopLevelStacks = topLevelStacks,
                    )
                }
            }
        )
    }
}

@Serializable
sealed class Route {
    sealed class TopLevel : Route()
}