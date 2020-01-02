package io.github.a2nr.jetpakcourse.utils

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.test.espresso.Espresso
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

fun waitUntilVisible(matcher: Matcher<View>) {
    var onLoading = true
    do {
        Espresso.onView(matcher)
            .check { view, _ ->
                if (view.isVisible)
                    onLoading = false
            }
        Thread.sleep(1000)
    } while (onLoading)
}

fun childAtPosition(
    parentMatcher: Matcher<View>, position: Int
): Matcher<View> {

    return object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("Child at position $position in parent ")
            parentMatcher.describeTo(description)
        }

        public override fun matchesSafely(view: View): Boolean {
            val parent = view.parent
            return parent is ViewGroup && parentMatcher.matches(parent)
                    && view == parent.getChildAt(position)
        }
    }
}
