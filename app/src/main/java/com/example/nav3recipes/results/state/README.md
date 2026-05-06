# Returning a Result (State-Based)

This recipe demonstrates how to return a result from one screen to a previous screen using a state-based approach.

## How it works

This example uses a `ResultEventBus` to manage the result as state.

1. **ResultEventBusNavEntryDecorator**: A `NavEntryDecorator` that provides a `ResultEventBus` via `LocalResultEventBus`.
2. **`ResultEventBus`**: A `ResultEventBus` is created and made available to the composables via `LocalResultEventBus`. This EventBus sends and receives the results.
3. **Setting the result**: The screen that produces the result calls `resultBus.sendResult(person)` to send the data back.
4. **Observing the result**: The screen that needs the result calls `resultBus.conflateAsState<Person?>()` to get a `State` object representing the result. The UI then observes this state and recomposes whenever the result changes.

This approach is suitable when only the latest result is required. The result state does not survive configuration change or process death.
