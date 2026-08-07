package com.example.nav3recipes.deeplink.usecases.serializer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.invoke
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.nav3recipes.common.deeplink.EntryScreen
import com.example.nav3recipes.common.deeplink.TextContent
import com.example.nav3recipes.ui.setEdgeToEdgeConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

internal const val PRODUCT_URI_PATTERN =
    "https://www.nav3recipes.com/products?product={product}&quantity={quantity}"

internal data class Product(val name: String, val color: Color)

@Serializable
internal data class ProductDetailsKey(
    @Serializable(with = ProductSerializer::class)
    val product: Product,
    val quantity: Int,
): NavKey

@Serializable
internal object FallbackKey: NavKey

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setEdgeToEdgeConfig()
        super.onCreate(savedInstanceState)

        val request = DeepLinkRequest(intent)
        val deepLinkMatcher = UriDeepLinkMatcher(
            PRODUCT_URI_PATTERN.toUri(),
            serializer<ProductDetailsKey>()
        )

        val matchResult = deepLinkMatcher.match(request)
        val key = matchResult?.key ?: FallbackKey

        setContent {
            val backStack: NavBackStack<NavKey> = rememberNavBackStack(key)
            NavDisplay(
                backStack = backStack,
                onBack = backStack::removeLastOrNull,
                entryProvider = entryProvider {
                    entry<ProductDetailsKey> { key ->
                        EntryScreen(key.product.name) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(key.product.color, shape = CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(key.quantity.toString(), color = Color.White)
                            }
                        }
                    }
                    entry<FallbackKey> {
                        EntryScreen("Fallback Key") {
                            TextContent(
                                "Failed to deep link - DeepLinkRequest " +
                                        "did not match with any DeepLinkMatcher"
                            )
                        }
                    }
                }
            )
        }
    }
}