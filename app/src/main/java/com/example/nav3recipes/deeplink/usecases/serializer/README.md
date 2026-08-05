# Custom DeepLinkSerializer Recipe

This recipe demonstrates how to use a custom `DeepLinkSerializer` in Navigation 3 using `UriDeepLinkMatcher` and Kotlinx Serialization.

## How it works

This recipe consists of two activities:
- `DeepLinkSerializerActivity`: Takes user input for product details (name, color, and quantity), serializes the custom `Product` object into a URI query parameter string (`?product={product}&quantity={quantity}`), constructs a `DeepLinkRequest` URI, and launches `MainActivity`.
- `MainActivity`: Constructs a `DeepLinkRequest(intent)`, matches it using `UriDeepLinkMatcher` configured with `ProductDetailsKey` (which uses `ProductSerializer` for `product` and native serialization for `quantity`), and displays the deserialized product details in `NavDisplay`.

## Key Concepts

1. **`DeepLinkSerializer<T>`**:
   Extends `DeepLinkSerializer<T>` to provide custom string serialization and deserialization logic for custom objects or types (such as `Color` or packed string formats) that need to be parsed from or encoded into URI path/query parameters.

2. **Annotating NavKey properties with `@Serializable(with = ...)`**:
   The custom `NavKey` uses `@Serializable(with = ProductSerializer::class)` on properties whose types require custom serialization (e.g. `product: Product`).

3. **Mixing Standard and Custom Serialized Parameters**:
   Parameters with standard types (like `quantity: Int`) are serialized natively out-of-the-box by Kotlinx Serialization, while custom parameters (like `product: Product`) use `ProductSerializer`.

4. **`UriDeepLinkMatcher` integration**:
   `UriDeepLinkMatcher(uriPattern, serializer<ProductDetailsKey>())` automatically applies custom and standard serializers when matching and decoding deep link URIs.
