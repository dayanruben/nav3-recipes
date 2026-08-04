package com.example.nav3recipes.deeplink.syntheticbackstack.util

import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import com.example.nav3recipes.common.deeplink.LIST_USERS
import com.example.nav3recipes.deeplink.syntheticbackstack.DEEPLINK_URL_TAG_USER
import com.example.nav3recipes.deeplink.syntheticbackstack.DEEPLINK_URL_TAG_USERS
import com.example.nav3recipes.deeplink.syntheticbackstack.Home
import com.example.nav3recipes.deeplink.syntheticbackstack.UserDetail
import com.example.nav3recipes.deeplink.syntheticbackstack.Users

/**
 * An implementation of [DeepLinkMatcher] that determines the key based on the deep link Uri's first
 * path segment.
 */
internal class SimpleDeepLinkMatcher: DeepLinkMatcher<NavKey, DeepLinkMatcher.MatchResult<NavKey>>() {
    override fun matchRequest(request: DeepLinkRequest): MatchResult<NavKey> {
        val paths = request.uri?.pathSegments

        // default to Home
        if (paths.isNullOrEmpty()) return MatchResult(Home)

        return when(paths.first()) {
            // "https://www.nav3deeplink.com/users"
            DEEPLINK_URL_TAG_USERS -> MatchResult(Users)
            // "https://www.nav3deeplink.com/user/$firstName/$location"
            DEEPLINK_URL_TAG_USER -> {
                val firstName = paths[1]
                val location = paths[2]
                val user = LIST_USERS.find {
                    it.firstName == firstName && it.location == location
                }
                if (user == null) MatchResult(Users) else MatchResult(
                    UserDetail(
                        user
                    )
                )
            }
            // "https://www.nav3deeplink.com/home"
            else -> MatchResult(Home) // default to Home
        }
    }
}