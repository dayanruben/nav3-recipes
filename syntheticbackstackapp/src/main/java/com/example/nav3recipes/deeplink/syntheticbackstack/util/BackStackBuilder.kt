package com.example.nav3recipes.deeplink.syntheticbackstack.util

import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.BackStackMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.navigation3.runtime.deeplink.withBackStack
import com.example.nav3recipes.deeplink.syntheticbackstack.NavDeepLinkRecipeKey

/**
 * Maps a [SimpleDeepLinkMatcher] to a [BackStackMatcher]s.
 *
 * The returned [BackStackMatcher] builds a backStack with [DeepLinkMatcher.withBackStack].
 *
 * This sample implementation of [DeepLinkMatcher.withBackStack] can return one of two possible backStacks:
 *
 * 1. a backStack with only the deep linked key if [buildFullPath] is false.
 * 2. a backStack containing the deep linked key and its hierarchical parent keys
 * if [buildFullPath] is true.
 *
 * In the context of this recipe, [buildFullPath] is true if the deeplink intent has the
 * [Intent.FLAG_ACTIVITY_NEW_TASK] and [Intent.FLAG_ACTIVITY_CLEAR_TASK]
 * flags.
 * These flags indicate that the deep linked Activity was started as the root Activity of a new Task, in which case
 * a full synthetic backStack is required in order to support the proper, expected back button behavior.
 *
 * If those flags were not present, it means the deep linked Activity was started
 * in the app that originally triggered the deeplink. In this case, that original app is assumed to
 * already have existing screens that users can system back into, therefore a synthetic backstack
 * is OPTIONAL.
 *
 * @param buildFullPath builds a full synthetic backStack if true. Otherwise, the back stack
 * only contains the deep link target key.
 */
internal fun SimpleDeepLinkMatcher.withBackStack(
    buildFullPath: Boolean
): BackStackMatcher<NavKey, NavKey> = withBackStack { matchResult ->
    val startKey = matchResult.key
    if (!buildFullPath) {
        listOf(matchResult.key)
    } else {
        /**
         * iterate up the parents of the startKey until it reaches the root key (a key without a parent)
         */
        buildList {
            var node: NavKey? = startKey
            while (node != null) {
                add(0, node)
                val parent = if (node is NavDeepLinkRecipeKey) {
                    node.parent
                } else null
                node = parent
            }
        }
    }
}