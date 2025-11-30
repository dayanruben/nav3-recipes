package com.example.nav3recipes.deeplink.app.util

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import com.example.nav3recipes.deeplink.app.DEEPLINK_URL_TAG_USER
import com.example.nav3recipes.deeplink.app.DEEPLINK_URL_TAG_USERS
import com.example.nav3recipes.deeplink.app.Home
import com.example.nav3recipes.deeplink.app.NavDeepLinkRecipeKey
import com.example.nav3recipes.deeplink.app.UserDetail
import com.example.nav3recipes.deeplink.app.Users
import com.example.nav3recipes.deeplink.common.LIST_USERS

/**
 * A function that build a synthetic backStack.
 *
 * This helper returns one of two possible backStacks:
 *
 * 1. a backStack with only the deeplinked key if [buildFullPath] is false.
 * 2. a backStack containing the deeplinked key and its hierarchical parent keys
 * if [buildFullPath] is true.
 *
 * In the context of this recipe, [buildFullPath] is true if the deeplink intent has the
 * [android.content.Intent.FLAG_ACTIVITY_NEW_TASK] and [android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK]
 * flags.
 * These flags indicate that the launcher Activity (MainActivity) was started as the root Activity of a new Task, in which case
 * a full synthetic backStack is required in order to support the proper, expected back button behavior.
 *
 * If those flags were not present, it means the launcher Activity (MainActivity) was started
 * in the app that originally triggered the deeplink. In this case, that original app is assumed to
 * already have existing screens that users can system back into, therefore a synthetic backstack
 * is OPTIONAL.
 *
 */
internal fun buildBackStack(
    startKey: NavKey,
    buildFullPath: Boolean
): List<NavKey> {
    if (!buildFullPath) return listOf(startKey)
    /**
     * iterate up the parents of the startKey until it reaches the root key (a key without a parent)
     */
    return buildList {
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

/**
 * A function that converts a deeplink uri into a NavKey.
 *
 * This helper is intentionally simple and basic. For a recipe that focuses on parsing a
 * deeplink uri into a NavKey, please see [com.example.nav3recipes.deeplink.basic].
 */
internal fun Uri?.toKey(): NavKey {
    if (this == null) return Home

    val paths = pathSegments

    if (pathSegments.isEmpty()) return Home

    return when(paths.first()) {
        DEEPLINK_URL_TAG_USERS -> Users
        DEEPLINK_URL_TAG_USER -> {
            val firstName = pathSegments[1]
            val location = pathSegments[2]
            val user = LIST_USERS.find {
                it.firstName == firstName && it.location == location
            }
            if (user == null) Users else UserDetail(user)
        }
        else -> Home
    }
}