# Deep Link Advanced Recipe

This recipe demonstrates how to build a synthetic backStack and support Back vs Up button
through managing the Task stack.

# How it works
This recipe simulates a real-world scenario where "App A" deeplinks
into "App B".

"App A" is simulated by the module [com.example.nav3recipes.deeplink.advanced], which
contains the [CreateAdvancedDeepLinkActivity] that allows you to create a deeplink intent and
trigger that in either the existing Task, or in a new Task.

"App B" is simulated by the module [com.example.nav3recipes.deeplink.app], which contains
the MainActivity that you deeplink into. That module shows you how to build a synthetic backStack
and how to manage the Task stack properly in order to support both Back and Up buttons.

# Synthetic BackStack
A backStack that simulates the user manually navigating from the root screen to the deeplink screen.
In general it should be built when the deeplinked Activity is started in a new
Task stack. See below sections for more details.

# Back Button
The Back button should direct users back the previous screen. However, what this "previous screen" 
is depends on whether the deeplinked screen was opened in the original Task (with App A as 
root Activity), or in a new Task.

In the first case with the original Task, the Back button should bring the user back to the 
screen that triggered the deeplink. Note: Depending on the actual
app, you can arguably also create a synthetic backstack here if you want a different back behavior.

In the second case with a new Task, the Back button should
bring the user to the hierarchical PARENT of the current screen. A synthetic backStack here
ensures that the user backs into the expected screen.

# Up Button
The Up button should direct the users to the hierarchical PARENT of the current screen. 
Furthermore, the Up button should never bring the user out of deeplinked app. 
This means the root screen should NOT have the Up button.

A synthetic backStack here ensures that users are correctly directed to the PARENT screen.
As mentioned, a synthetic backStack should be built / is built when starting a new Task. 
This means that if the deeplinked Activity were started in the original Task, the
Activity should be restarted with Intent.FLAG_ACTIVITY_NEW_TASK. This flag would trigger the
Activity to restart in a new Task, which in turn should trigger the building of a synthetic backStack.

## Task & backStack illustration

**Original Task**
| Task        | Target                         | Synthetic backStack                            |
|-------------|--------------------------------|------------------------------------------------|
| Up Button   | Deeplinked Screen's Parent     | Restart Activity on new Task & build backStack |
| Back Button | Screen that triggered deeplink | None                                           |

**New Task**
| Task        | Target                         | Synthetic backStack                            |
|-------------|--------------------------------|------------------------------------------------|
| Up Button   | Deeplinked Screen's Parent     | Build backStack on Activity creation           |
| Back Button | Deeplinked Screen's Parent     | Build backStack on Activity creation           |