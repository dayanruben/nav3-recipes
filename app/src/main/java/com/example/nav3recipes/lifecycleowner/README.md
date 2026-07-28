# LocalLifecycleOwner Recipe

This recipe demonstrates how to use `LifecycleResumeEffect` in Navigation 3 entries to pause and resume work based on the entry's lifecycle state.

## How it works

In Navigation 3, by default each `NavEntry` is provided its own `LifecycleOwner` via `LocalLifecycleOwner.current`. This means that any lifecycle-aware components inside the entry is automatically scoped to the `NavEntry`.

### `LifecycleResumeEffect` with Dialog Scenes

1. **RouteA (Screen)**:
   - Uses `LifecycleResumeEffect(Unit)` scoped to the `NavEntry`'s `LocalLifecycleOwner.current` to advance the`LinearProgressIndicator` while in the `RESUMED` state.
   - Automatically resets `progressValue` back to `0f` whenever it hits `1f`.

2. **RouteB (Dialog)**:
   - Configured as a dialog using `DialogSceneStrategy.dialog()`.
   - When the user opens the RouteB dialog, RouteA remains visible behind the dialog in the `STARTED` state (leaving `RESUMED`).
   - `LifecycleResumeEffect` calls `onPauseOrDispose`, pausing the progress indicator.
   - When the dialog is dismissed, RouteA returns to `RESUMED`, and `LifecycleResumeEffect` resumes the progress indicator automatically.
