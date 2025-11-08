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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.serialization.NavKeySerializer
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import com.example.nav3recipes.ui.setEdgeToEdgeConfig
import kotlinx.serialization.Serializable
import kotlin.collections.last


@Serializable
data object RouteA : NavKey

@Serializable
data object RouteA1 : NavKey

@Serializable
data object RouteB : NavKey

@Serializable
data class RouteB1(val id: String) : NavKey

@Serializable
data object RouteC : NavKey

@Serializable
data object RouteC1 : NavKey

private val TOP_LEVEL_ROUTES = mapOf<NavKey, NavBarItem>(
    RouteA to NavBarItem(icon = Icons.Default.Home, description = "Route A"),
    RouteB to NavBarItem(icon = Icons.Default.Face, description = "Route B"),
    RouteC to NavBarItem(icon = Icons.Default.Camera, description = "Route C"),
)

data class NavBarItem(
    val icon: ImageVector,
    val description: String
)

class MultipleStacksActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setEdgeToEdgeConfig()
        super.onCreate(savedInstanceState)
        setContent {
            val navigationState = rememberNavigationState(
                startRoute = RouteA,
                topLevelRoutes = TOP_LEVEL_ROUTES.keys
            )

            val navigator = remember { Navigator(navigationState) }

            val entryProvider = entryProvider {
                featureASection(onSubRouteClick = { navigator.navigate(RouteA1) })
                featureBSection(onDetailClick = { id -> navigator.navigate(RouteB1(id)) })
                featureCSection(onSubRouteClick = { navigator.navigate(RouteC1) })
            }

            Scaffold(bottomBar = {
                NavigationBar {
                    TOP_LEVEL_ROUTES.forEach { (key, value) ->
                        val isSelected = key == navigationState.topLevelRoute
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { navigator.navigate(key) },
                            icon = {
                                Icon(
                                    imageVector = value.icon,
                                    contentDescription = value.description
                                )
                            },
                            label = { Text(value.description) }
                        )
                    }
                }
            }) { paddingValues ->
                NavDisplay(
                    entries = navigationState.toEntries(entryProvider),
                    onBack = { navigator.goBack() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

/**
 * Convert NavigationState into NavEntries.
 */
@Composable
fun NavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>
): SnapshotStateList<NavEntry<NavKey>> {

    val decoratedEntries = backStacks.mapValues { (_, stack) ->
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
        )
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = decorators,
            entryProvider = entryProvider
        )
    }

    return stacksInUse
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}

/**
 * Handles navigation events (forward and back) by updating the navigation state.
 */
class Navigator(val state: NavigationState){
    fun navigate(route: NavKey){
        if (route in state.backStacks.keys){
            // This is a top level route, just switch to it
            state.topLevelRoute = route
        } else {
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }
    
    fun goBack(){
        
        val currentStack = state.backStacks[state.topLevelRoute] ?:
            error("Stack for $state.topLevelRoute not found")
        val currentRoute = currentStack.last()

        // If we're at the base of the current route, go back to the start route stack.
        if (currentRoute == state.topLevelRoute){
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLast()
        }
    }
}

/**
 * Create a navigation state that persists config changes and process death.
 */
@Composable 
fun rememberNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>
) : NavigationState {

    val topLevelRoute = rememberSerializable(
        serializer = MutableStateSerializer(NavKeySerializer())
    ){
        mutableStateOf(startRoute)
    }

    return NavigationState(
        topLevelRoute = topLevelRoute,
        backStacks = topLevelRoutes.associateWith { key ->
            rememberNavBackStack(key)
        }
    )
}

/**
 * State holder for navigation state.
 *
 * @param topLevelRoute - the current top level route
 * @param backStacks - the back stacks for each top level route
 */
class NavigationState(
    topLevelRoute: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>
) {
    val startRoute = topLevelRoute.value
    var topLevelRoute : NavKey by topLevelRoute
    val stacksInUse : List<NavKey>
        get(){
            val stacksInUse = mutableListOf(startRoute)
            if (this@NavigationState.topLevelRoute != startRoute) stacksInUse += this@NavigationState.topLevelRoute
            return stacksInUse
        }
}


