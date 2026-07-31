# Deep Link Advanced Recipe

This recipe demonstrates how to apply the principles of navigation in the context of deep links by
managing a synthetic backStack and Task stacks.

# Recipe Structure
This recipe simulates a real-world scenario where "App A" deep links
into "App B".

"App A" is simulated by the module [com.example.nav3recipes.deeplink.handlerequests.advanced](/app/src/main/java/com/example/nav3recipes/deeplink/handlerequests/advanced), which
contains the `AdvancedCreateDeepLinkActivity` that allows you to create a deeplink intent and
trigger that in either the existing Task, or in a new Task.

"App B" is simulated by the module [advanceddeeplinkapp](/advanceddeeplinkapp/src/main/java/com/example/nav3recipes/deeplink/advanced), which contains
the MainActivity that you deeplink into. That module shows you how to build a synthetic backStack
and how to manage the Task stack properly in order to support both Back and Up buttons.

# How to Use
Ensure both the main `app` and `advanceddeeplinkapp` are installed on the emulator or connected device. Ensure that the installed `advanceddeeplinkapp` supports
the `"www.nav3deeplink.com"` link.

On the recipe's landing page, choose the filters and click the button to deep link. It should bring you to the Activity of `advanceddeeplinkapp`.

# Core implementation
The core helper functions for navigateUp and building synthetic backStack can be
found [here](/advanceddeeplinkapp/src/main/java/com/example/nav3recipes/deeplink/advanced/util/DeepLinkBackStackUtil.kt)

# Further Read
Check out the [deep link guide](/docs/deeplink-guide.md) for a 
comprehensive guide on Deep linking principles and how to apply them in Navigation 3.
