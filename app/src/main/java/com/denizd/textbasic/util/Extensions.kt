package com.denizd.textbasic.util

import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

/**
 * Retrieves a specified colour customised to the currently applied theme.
 *
 * @param id    attribute ID of the colour reference
 * @return      resolved colour
 */
fun Resources.Theme.getThemedColor(@AttrRes id: Int): Int = TypedValue().also { value ->
    resolveAttribute(id, value, true)
}.data

/**
 * Show a snack bar. Colours will be applied according to the theme given.
 *
 * @receiver            theme to apply colours of
 * @param view          view where the snack bar will be shown in
 * @param text          text to present in the snack bar
 * @param anchor        view to anchor the snack bar to
 * @param actionText    text to show on the action button; button will only be shown if a value is
 *                      passed here
 * @param action        action to execute if the action button is pressed
 */
fun Resources.Theme.showSnackBar(
    view: CoordinatorLayout,
    text: String,
    anchor: View? = null,
    actionText: String = "",
    action: () -> Unit = {},
) {
    val s = Snackbar.make(view, text, Snackbar.LENGTH_SHORT)
        // Set colours
        .setTextColor(getThemedColor(com.google.android.material.R.attr.colorOnSurfaceInverse))
        .setBackgroundTint(getThemedColor(com.google.android.material.R.attr.colorSurfaceInverse))

    if (actionText.isNotBlank()) {
        // Set action, if a value for the text has been passed
        s.setAction(actionText) {
            action()
        }

        // Show this snack bar for a longer time for the user to have a chance to react
        s.duration = Snackbar.LENGTH_LONG
    }

    // Set anchor view, if one has been passed
    anchor?.let { a -> s.setAnchorView(a) }
    s.show()
}