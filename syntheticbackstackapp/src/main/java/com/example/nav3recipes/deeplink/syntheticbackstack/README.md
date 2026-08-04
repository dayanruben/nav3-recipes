# Deep Link Synthetic BackStack App

The sample app that parses a deep link and builds a synthetic back stack based on the task stack state.

# Recipe Structure

The `util` package contains:

1. `BackStackBuilder` - a `withBackStack` extension function that builds the synthetic back stack
2. `SimpleDeepLinkMatcher` - a custom `DeepLinkMatcher` that parses the uri into a navigation key
3. `BackStackUril` - contains the implementation of `Up` button and a helper to create a new task stack

The main package contains:
1. `NavRecipeKey` - The navigation keys
2. `SyntheticBackStackAppActivity` - The app activity that parses the `Intent` and calls `withBackStack` to build an initial stack.

# Further Read
Check out the [deep link guide](/docs/deeplink-guide.md) for a
comprehensive guide on Deep linking principles and how to apply them in Navigation 3.