package com.example.nav3recipes.deeplink.staticuri

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.net.toUri
import androidx.lifecycle.compose.dropUnlessResumed
import com.example.nav3recipes.common.deeplink.EntryScreen
import com.example.nav3recipes.common.deeplink.PaddedButton
import com.example.nav3recipes.common.deeplink.TextContent
import com.example.nav3recipes.deeplink.basic.ui.PATH_BASE
import com.example.nav3recipes.ui.setEdgeToEdgeConfig

const val HOME_URI = "$PATH_BASE/home"

class StaticUriDeepLinkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setEdgeToEdgeConfig()
        super.onCreate(savedInstanceState)

        setContent {
            EntryScreen("Deep link url:") {
                TextContent(HOME_URI)
                PaddedButton("Deeplink Away!", onClick = dropUnlessResumed {
                    val intent = Intent(
                        this@StaticUriDeepLinkActivity,
                        MainActivity::class.java
                    )
                    // the uri to deep link with
                    intent.data = HOME_URI.toUri()
                    startActivity(intent)
                })
            }
        }
    }
}
