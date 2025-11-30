package com.example.nav3recipes.deeplink.app.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.example.nav3recipes.deeplink.app.NavDeepLinkRecipeKey

/**
 * A Navigator class that supports adding and removing NavKeys from a NavBackStack.
 *
 * This class stores the [Context] and [Activity] required to support navigating up (Up button).
 */
class Navigator(
    private val backStack: NavBackStack<NavKey>,
    private val context: Context
) {
    private val activity: Activity? =
        generateSequence(context) { (it as? ContextWrapper)?.baseContext }
            .mapNotNull { it as? Activity }
            .firstOrNull()

    fun add(key: NavKey) {
        backStack.add(key)
    }

    fun pop() {
        backStack.removeLastOrNull()
    }

    /**
     * If this app was started on its own Task stack, then navigate up would simply
     * pop from the backStack.
     *
     * Otherwise, it will restart this app in a new Task and build a full synthetic backStack
     * starting from the root key to current key's parent (current key is "popped" upon user click on up button).
     * This operation is required because by definition, an Up button is expected to:
     * 1. Move from current screen to its hierarchical parent
     * 2. Stay within this app
     *
     * Therefore, we need to build a synthetic backStack to fulfill expectation 1., and we need to
     * restart the app in its own Task so that this app's screens are displayed within
     * this app instead of being displayed within the originating app that triggered the deeplink.
     */
    fun navigateUp() {
        /**
         * The root key (the first key on synthetic backStack) would/should never display the Up button.
         * So if the backStack only contains a non-root key, it means a synthetic backStack had not
         * been built (aka the app was opened in the originating Task).
         */
        if (backStack.size == 1) {
            val currKey = backStack.last()
            /**
             * upon navigating up, the current key is popped, so the restarted activity
             * lands on the current key's parent
             */
            val deeplinkKey = if (currKey is NavDeepLinkRecipeKey) {
                currKey.parent
            } else null

            /**
             * create a [androidx.core.app.TaskStackBuilder] that will restart the
             * Activity as the root Activity of a new Task
             */
            val builder = createTaskStackBuilder(deeplinkKey, activity, context)
            // ensure current activity is finished
            activity?.finish()
            // trigger restart
            builder.startActivities()
        } else {
            pop()
        }
    }
}

/**
 *  Creates a [androidx.core.app.TaskStackBuilder].
 *
 *  The builder takes the current context and Activity and builds a new Task stack with the
 *  [activity] as the root Activity. The resulting TaskStack is used to restart
 *  the Activity in its own Task.
 */
private fun createTaskStackBuilder(
    deeplinkKey: NavKey?,
    activity: Activity?,
    context: Context
): TaskStackBuilder {
    /**
     * The intent restarts the current activity. If activity were null, then it will
     * attempt to default to the Activity from getLaunchIntentForPackage.
     */
    val intent = if (activity != null) {
        Intent(context, activity.javaClass)
    } else {
        val launchIntent =
            context.packageManager.getLaunchIntentForPackage(context.packageName)
        launchIntent ?: Intent()
    }

    /**
     * Pass in the deeplink url of the target key so that upon restart, the app
     * can build the synthetic backStack starting from the deeplink key all the way up to the
     * root key.
     *
     * See [buildBackStack] for building synthetic backStack.
     */
    if (deeplinkKey != null && deeplinkKey is NavDeepLinkRecipeKey) {
        intent.data = deeplinkKey.deeplinkUrl.toUri()
    }

    /**
     * Ensure that the MainActivity is restarted as the root of a new Task
     */
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

    /**
     * Lastly, attach the intent to the TaskStackBuilder.
     *
     * By using `addNextIntentWithParentStack`, the TaskStackBuilder will automatically
     * add the intents for the parent activities (if any) of [activity].
     */
    return TaskStackBuilder.create(context).addNextIntentWithParentStack(intent)
}