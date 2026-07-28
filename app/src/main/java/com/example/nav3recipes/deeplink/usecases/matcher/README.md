# Custom DeepLinkMatcher Recipe

This recipe demonstrates how to create a custom `DeepLinkMatcher` in Navigation 3 using custom request extras and Kotlinx Serialization.

## How it works

This recipe consists of two activities:
- `CustomDeepLinkMatcherActivity`: Accepts user input, serializes a `HomeKey` instance into JSON, attaches it to an `Intent` extra via a `RequestExtrasKey`, and launches `MainActivity`.
- `MainActivity`: Constructs a `DeepLinkRequest(intent)`, evaluates it with `JsonDeepLinkMatcher`, decodes the `HomeKey`, and sets it as the starting route in `NavDisplay`.

## Key Concepts

1. **Custom `RequestExtrasKey`**:
   `JsonDeepLinkMatcherKey` defines a custom extra key implementing `RequestExtrasKey<String>` to type-safely store and read serialized JSON payloads in `DeepLinkRequest.extras`.

2. **Custom `DeepLinkMatcher`**:
   `JsonDeepLinkMatcher<T>` extends `DeepLinkMatcher<T, MatchResult<T>>` and implements `matchRequest(request)` to extract `request.extras[JsonDeepLinkMatcherKey]` and decode it into a strongly typed `NavKey` using Kotlinx Serialization.
