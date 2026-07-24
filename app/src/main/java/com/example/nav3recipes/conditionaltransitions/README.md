# Conditional Transitions Recipe

This recipe demonstrates how to create route-dependent screen transitions in Navigation 3 using `transitionSpec` and `popTransitionSpec`. The slide directions (right, left, bottom, top) are conditionally selected based on pairs of `initialState` and `targetState` navigation keys.

## How it works

- **Route Definitions**: Navigation destinations (`Step1`, `Step2`, `Step3`, `Step4`) are defined using a sealed class hierarchy implementing `NavKey` and marked with `@Serializable`.
- **Conditional Forward Transitions (`transitionSpec`)**: Matches pairs of `(initialKey to targetKey)` to determine the direction of the slide animation:
  - `Step1` $\rightarrow$ `Step2`: Swipes to the left
  - `Step2` $\rightarrow$ `Step3`: Swipes to the up
  - `Step3` $\rightarrow$ `Step4`: Swipes to the right
  - `Step4` $\rightarrow$ `Step1`: Slides to the bottom (restarts flow)
- **Conditional Pop Transitions (`popTransitionSpec`)**: Handles reverse slide directions when navigating back or when clearing the backstack.
- **Backstack Control**: Demonstrates clearing the navigation stack on the final step (`backStack.clear()` & `backStack.add(Step1)`) while executing a seamless top-slide transition.
