package com.example.nav3recipes.deeplink.usecases.serializer

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.navigation3.runtime.deeplink.DeepLinkSerializer
import kotlinx.serialization.SerializationException

internal object ProductSerializer : DeepLinkSerializer<Product>() {
    override val serialName: String = "com.example.nav3recipes.deeplink.usecases.serializer.Product"

    override fun serialize(value: Product): String = Uri.encode("${value.name}-${value.color.toArgb()}")

    override fun deserialize(value: String): Product {
        val decodedValue = Uri.decode(value)
        val splitArgs = decodedValue.split("-", limit = 2)
        if (splitArgs.size < 2) {
            throw SerializationException("Invalid product format: $decodedValue")
        }
        val name = splitArgs[0]
        val colorCode = splitArgs[1].toIntOrNull()
            ?: throw SerializationException("Invalid color code: ${splitArgs[1]}")
        val color = Color(colorCode)
        return Product(name, color)
    }
}
