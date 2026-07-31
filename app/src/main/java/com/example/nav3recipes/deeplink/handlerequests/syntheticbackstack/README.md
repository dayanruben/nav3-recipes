# Deep Link Synthetic BackStack Recipe

This recipe demonstrates how to apply the principles of navigation in the context of deep links by
managing a synthetic backStack and Task stacks.

# Recipe Structure
This recipe simulates a real-world scenario where "App A" deep links
into "App B".

"App A" is simulated by the module [syntheticbackstack](/app/src/main/java/com/example/nav3recipes/deeplink/handlerequests/syntheticbackstack), which
contains the `SyntheticBackStackDeepLinkActivity` that allows you to create a deeplink intent and
trigger that in either the existing Task, or in a new Task.

"App B" is simulated by the module [syntheticbackstackapp](/syntheticbackstackapp/src/main/java/com/example/nav3recipes/deeplink/syntheticbackstack), which contains
the MainActivity that you deeplink into. That module shows you how to build a synthetic backStack
and how to manage the Task stack properly in order to support both Back and Up buttons.

# How to Use
Ensure both the main `app` and `syntheticbackstackapp` are installed on the emulator or connected device. Ensure that the installed `syntheticbackstackapp` supports
the `"www.nav3deeplink.com"` link.

On the recipe's landing page, choose the filters and click the button to deep link. It should bring you to the Activity of `syntheticbackstackapp`.

# How it Works
The recipe follows the deep link guideline summarized [here](/docs/deeplink-guide.md#summary).

To see behavior of `Existing Task`:
1. Open deep link using current task
2. On the device, swipe up to see all recent apps
3. Notice that the new Activity is opened within the Nav3Recipes app
4. Click back button to go back to the original Activity
5. Repeat step 1
6. Click the up button to go to parent screen
7. On the device, swipe up to see all recent apps
8. Notice that the new Activity is now opened within the Nav3SyntheticBackStack app

To see behavior of `New Task`:
1. Open deep link using new task
2. On the device, swipe up to see all recent apps
3. Notice that the new Activity is opened within the Nav3SyntheticBackStack app
4. Click Up or Back button to go to parent screen

# Core implementation
The core helper functions for navigateUp and building synthetic backStack can be
found [here](/syntheticbackstackapp/src/main/java/com/example/nav3recipes/deeplink/syntheticbackstack/util/DeepLinkBackStackUtil.kt)

# Further Read
Check out the [deep link guide](/docs/deeplink-guide.md) for a 
comprehensive guide on Deep linking principles and how to apply them in Navigation 3.
