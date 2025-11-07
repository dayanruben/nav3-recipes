# Common navigation UI / Multiple back stacks recipe #

This recipe demonstrates how to create top level routes with their own back stack. 

The app has three top level routes: `RouteA`, `RouteB` and `RouteC`. These routes have sub routes `RouteA1`, `RouteB1` and `RouteC1` respectively. The content for the sub routes is a counter that can be used to verify state retention through configuration changes and process death.

The app's navigation state is managed by the `Navigator` class. This maintains a back stack for each top level route and holds the logic for navigating within and between these back stacks. 

The state for each `NavEntry` is retained while its key is in the associated back stack. This means that even when the top level route changes, the state for entries in other back stacks will be retained. 

The `Navigator` class is split into two areas of responsibility: 

- **Managing navigation state**. This is done in pure Kotlin, no Composable functions. A `Saver` allows the navigation state to be saved and restored.
- **Providing UI**. The `NavEntry`s for the current navigation state are provided using the `entries` Composable function. This can be used directly with `NavDisplay`.

Key behaviors: 

- This app follows the "exit through home" pattern where the user always exits through the starting back stack. This means that `RouteA`'s entries are _always_ in the list of entries. 
- Navigating to a top level route that is not the starting route _replaces_ the other entries. For example, navigating A->B->C would result in entries for A+C, B's entries are removed. 

Important implementation details: 

- Each top level route has its own `SaveableStateHolderNavEntryDecorator`. This is the object responsible for managing the state for the entries in its back stack. 
