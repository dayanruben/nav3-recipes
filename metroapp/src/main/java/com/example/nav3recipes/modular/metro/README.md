# Modular Navigation Recipe (Metro)

This recipe demonstrates how to structure a multi-module application using Navigation 3 and [Metro](https://zacsweers.github.io/metro/) for dependency injection. The goal is to create a decoupled architecture where navigation is defined and implemented in separate feature modules.

## How it works

The application is divided into several modules:

-   **`app` module**: This is the main application module. It provides `MetroDeepLinkActivity` (an entry point demonstrating regular and deep link intent launches), `MetroModularActivity` (which hosts the `NavDisplay` and delegates deep link intents to `Navigator.handleDeepLink(intent)`), the start destination binding, and builds the final `entryProvider`.

-   **`common` module**: This module contains the core navigation logic, including:
    -   A `Navigator` class that manages the back stack, resolves incoming deep link intents, and builds synthetic back stacks via `handleDeepLink(intent)`.
    -   A `startDestination: NavKey` used to initialize the back stack.
    -   An `EntryProviderInstaller` type, which is a function that feature modules use to contribute their navigation entries to the application's `entryProvider`.
    -   `BackStackMatcher<*, NavKey>`, which feature modules use to contribute their deep link matchers with synthetic back stacks.

-   **Feature modules (e.g., `conversation`, `profile`)**: Each feature is split into two sub-modules:
    -   **`api` module**: Defines the public API for the feature, including its navigation routes and deep link URI patterns. This allows other modules to navigate to this feature without needing to know about its implementation details.
    -   **`impl` module**: Provides the implementation of the feature, including its composables, an `EntryProviderInstaller` that maps the feature's routes to its composables, and a `BackStackMatcher<*, NavKey>` configured with `.withBackStack` to establish the appropriate hierarchical back stack (e.g., `[ConversationList, ConversationDetail(id)]`). Both are provided using Metro's `@ContributesTo` and `@IntoSet` annotations.

This modular approach allows for a clean separation of concerns, making the codebase more scalable and maintainable. Each feature is responsible for its own navigation logic, and the `app` module only combines these pieces together.
